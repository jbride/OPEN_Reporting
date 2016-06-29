import mysql.connector
import requests
import requests
import json
import datetime


def get_new_completions(creds):
    """connect to datawarehouse and retrieve new completions"""
    connection = mysql.connector.connect(**creds)
    cursor = connection.cursor()
    query = ("""SELECT StudentAccreditations.AccreditationDate, Students.Email, Courses.CourseID
         FROM StudentAccreditations
         INNER JOIN Students
         ON StudentAccreditations.StudentID=Students.StudentID
         INNER JOIN Courses
         ON Courses.CourseID=StudentAccreditations.CourseID""")
    cursor.execute(query)
    return [i for i in cursor]

def get_pg_emails(creds):
    """check for user existence in PG with email"""
    pg_users_url = 'http://api.redhat.prepathgather.com/v1/users/'
    response = requests.get(pg_users_url, headers=creds)
    response_as_json = response.json()
    users = response_as_json['results']
    return [user['email'] for user in users]

def get_incomplete_pg_user_content_objs():
    """returns list of incomplete user_content objects"""
    pg_url = 'http://api.redhat.prepathgather.com/v1/user_content/'
    response = requests.get(pg_url, headers=pg_headers)
    response_as_json = response.json()
    content_objects = response_as_json['results']
    # incompletes = [obj for obj in content_objects if not obj['completed_at']]
    return [obj for obj in content_objects if not obj['completed_at']]

def mark_complete():
    """gets incomplete user_content objects and sets the 'completed_at' field \
       with a timestamp string"""
    for obj in completed_at_list:
        # make json with completed_at = 'now'
        data = {
            "content_id": obj['content_id'],
            "user_email": obj['user_email'],
            "user_id": obj['user_id'],
            "completed_at": datetime.now
          }
        # figure this syntax out/post with requests
        requests.post(pg_url, obj, headers=headers)
    return 200

def create_pg_user_content_obj(comp, course_id):
    data = {
        "content_id": course_id,
        "user_email": comp[1],
        "completed_at": comp[0]
      }
    return data

def get_pg_user_content_id(course_name, incompletes):
    for inc in incompletes:
        if course_name == inc['content']['name']:
            course_id = inc['id']
            return course_id
        else:
            return "match not found"


def main():
    new_completions = get_new_completions(datawarehouse)
    incompletes = get_incomplete_pg_user_content_objs()
    # new_completions is a list of tuples
    for completion in new_completions:
        course_name = completion[2]
        # find course_name in incompletes
        # incompletes is a list of dicts
        course_id = get_pg_user_content_id(course_name, incompletes)
        create_pg_user_content_obj(completion, course_id)


if __name__ == '__main__':
    from config import datawarehouse
    from config import pg_headers


    main()
