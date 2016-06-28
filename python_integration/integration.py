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

def verify_pg_user(creds, completer_email):
    """check for user existence in PG with email"""
    pg_users_url = 'http://api.redhat.prepathgather.com/v1/users/'
    response = requests.get(pg_users_url, headers=creds)
    response_as_json = response.json()
    users = response_as_json['results']
    emails = [user['email'] for user in users]
    if completer_email in emails:
        return True
    else:
        return False

def mark_complete():
    print('completing!')

def main():
    from config import datawarehouse
    from config import pg_headers
    new_completions = get_new_completions(datawarehouse)
    # new_completions is a list of tuples
    # slicing for efficiency of testing
    for user in new_completions[:10]:
        if verify_pg_user(pg_headers, user[1]):
            mark_complete()
        else:
            print("email not found")






if __name__ == '__main__':
    main()
