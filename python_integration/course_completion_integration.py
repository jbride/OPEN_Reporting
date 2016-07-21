def get_new_completions(creds):
    """connect to datawarehouse and retrieve new completions"""
    connection = mysql.connector.connect(**creds)
    cursor = connection.cursor()
    query = ("""SELECT StudentCourses.AssessmentDate, Students.Email, Courses.CourseID
         FROM StudentCourses
         INNER JOIN Students
         ON StudentCourses.StudentID=Students.StudentID
         INNER JOIN Courses
         ON Courses.CourseID=StudentCourses.CourseID
         WHERE Email LIKE '%redhat.com'
         AND StudentCourses.CreateDate > DATE_SUB(NOW(), INTERVAL 1 HOUR)""")
    cursor.execute(query)
    return [i for i in cursor]

def create_pg_user_content_obj(comp):
    data = {
        "content_id": comp[2],
        "user_email": comp[1],
        "completed_at": str(comp[0])
      }
    data = json.dumps(data)
    return data

def mark_complete(obj):
    """gets incomplete user_content objects and sets the 'completed_at' field \
       with a timestamp string"""
    pg_url = 'http://{}/v1/user_content/'.format(pathgather_url)
    req = requests.post(pg_url, headers=pg_headers, data=obj)
    if req.status_code == 404:
        logging.error(": " + str(req.status_code) + " " + obj)
    else:
        logging.info(": " + str(req.status_code))

def create_log_file():
    week = date.today().isocalendar()[1]
    year = date.today().year
    filename = '{}/{}-week{}.log'.format(log_path, year, week)
    logging.basicConfig(filename=filename, level=logging.INFO)
    logging.info('Started')

def main():
    create_log_file()
    new_completions = get_new_completions(datawarehouse)
    if not new_completions:
        logging.info('Nothing to report' + log_line_end)
    else:
        completions_for_posting = []
        logging.info('Completions for posting: {}'.format(len(new_completions)))
        for completion in new_completions:
            new_comp = create_pg_user_content_obj(completion)
            completions_for_posting.append(new_comp)
        for completion in completions_for_posting:
            mark_complete(completion)
        logging.info("job complete" + log_line_end)

if __name__ == '__main__':
    from config import *
    from datetime import date
    import mysql.connector
    import requests
    import json
    import schedule
    import time
    import logging

    main()
    schedule.every().minute.do(main)

    while True:
        schedule.run_pending()
        time.sleep(1)
