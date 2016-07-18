def get_new_completions(creds):
    """connect to datawarehouse and retrieve new completions"""
    connection = mysql.connector.connect(**creds)
    cursor = connection.cursor()
    query = ("""SELECT StudentAccreditations.AccreditationDate, Students.Email, Courses.CourseID
         FROM StudentAccreditations
         INNER JOIN Students
         ON StudentAccreditations.StudentID=Students.StudentID
         INNER JOIN Courses
         ON Courses.CourseID=StudentAccreditations.CourseID
         WHERE Email LIKE '%redhat.com'
         WHERE StudentAccreditations.AccreditationDate > DATE_SUB(NOW(), INTERVAL 1 HOUR)""")
    cursor.execute(query)
    return [i for i in cursor]
# 
# def get_pg_content_id(course_name):
#     """returns list of content objects"""
#     pg_url = 'http://api.redhat.prepathgather.com/v1/content/{}'.format(course_name)
#     response = requests.get(pg_url, headers=pg_headers)
#     response_as_json = response.json()
#     if 'id' in response_as_json.keys():
#         content_id = response_as_json['id']
#         return content_id

def create_pg_user_content_obj(comp, course_id):
    data = {
        "content_id": course_id,
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
        completed_course_id = completion[2]
        new_comp = create_pg_user_content_obj(completion, completed_course_id)
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

    schedule.every().hour.do(main)

    while True:
        schedule.run_pending()
        time.sleep(1)
    main()
