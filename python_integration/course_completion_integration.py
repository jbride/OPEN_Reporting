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

def get_pg_content_id(course_name):
    """returns list of content objects"""
    pg_url = 'http://api.redhat.prepathgather.com/v1/content/{}'.format(course_name)
    response = requests.get(pg_url, headers=pg_headers)
    response_as_json = response.json()
    if 'id' in response_as_json.keys():
        content_id = response_as_json['id']
        return content_id

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
    pg_url = 'http://api.redhat.prepathgather.com/v1/user_content/'
    req = requests.post(pg_url, headers=pg_headers, data=obj)
    print(obj, req.status_code)

def main():
    new_completions = get_new_completions(datawarehouse)
    completions_for_posting = []
    for completion in new_completions:
        new_comp = create_pg_user_content_obj(completion)
        completions_for_posting.append(new_comp)
    for completion in completions_for_posting:
        mark_complete(completion)


if __name__ == '__main__':
    from config import datawarehouse
    from config import pg_headers
    import mysql.connector
    import requests
    import json
    import schedule
    import time

    main()
    schedule.every().hour.do(main)

    while True:
        schedule.run_pending()
        time.sleep(1)
