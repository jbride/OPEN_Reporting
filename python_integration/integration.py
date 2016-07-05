def get_new_completions(creds):
    """connect to datawarehouse and retrieve new completions"""
    connection = mysql.connector.connect(**creds)
    cursor = connection.cursor()
    query = ("""SELECT StudentAccreditations.AccreditationDate, Students.Email, Courses.CourseName
         FROM StudentAccreditations
         INNER JOIN Students
         ON StudentAccreditations.StudentID=Students.StudentID
         INNER JOIN Courses
         ON Courses.CourseID=StudentAccreditations.CourseID""")
        #  WHERE StudentAccreditations.AccreditationDate > DATE_SUB(CURDATE(), INTERVAL 1 DAY)""")
    cursor.execute(query)
    return [i for i in cursor]

def get_pg_content_objs():
    """returns list of content objects"""
    pg_url = 'http://api.redhat.prepathgather.com/v1/content/'
    response = requests.get(pg_url, headers=pg_headers)
    response_as_json = response.json()
    content_objects = response_as_json['results']
    return content_objects

def get_pg_content_id(course_name, content_list):
    for content in content_list:
        if course_name in content.values():
            return content['id']
        else:
            return False

def create_pg_user_content_obj(comp, course_id):
    print("marking")
    data = {
        "content_id": course_id,
        "user_email": comp[1],
        "completed_at": "now"#comp[0]
      }
    data = json.dumps(data)
    return data

def mark_complete(obj):
    """gets incomplete user_content objects and sets the 'completed_at' field \
       with a timestamp string"""
    print("making request")
    pg_url = 'http://api.redhat.prepathgather.com/v1/user_content/'
    r = requests.post(pg_url, headers=pg_headers, data=obj)
    print(obj, r.status_code)

def main():
    new_completions = get_new_completions(datawarehouse)
    content_list = get_pg_content_objs()

    completions_for_posting = []
    print("i made it this far!")
    for completion in new_completions:
        completed_course_name = completion[2]
        course_id = get_pg_content_id(completed_course_name, content_list)
        if course_id:
            print("making completions")
            new_comp = create_pg_user_content_obj(completion, course_id)
            completions_for_posting.append(new_comp)
    for completion in completions_for_posting:
        mark_complete(completion)


if __name__ == '__main__':
    from config import datawarehouse
    from config import pg_headers
    import mysql.connector
    import requests
    import json


    print("hello")
    # schedule.every().day.do(main)
    #
    # while True:
    #     schedule.run_pending()
    #     time.sleep(1)
    main()
