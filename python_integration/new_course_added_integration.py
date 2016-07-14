def get_courses_from_dw(creds):
    """connect to datawarehouse and retrieve new completions"""
    connection = mysql.connector.connect(**creds)
    cursor = connection.cursor()
    query = ("""SELECT *
         FROM Courses""")
    cursor.execute(query)
    print("got courses") #print test
    return [i for i in cursor]

def check_for_new_courses(dw, pg):
    print("checking for new courses")
    return [course for course in dw if course not in pg]

def create_pg_content_obj(course):
    data = {
        'name': course[1],
        'content_type': 'Course',
        'souce_url': '',
        'provider_name': 'Dokeos',
        'topic_name': '',
        'custom_id': course[0]
    }
    data = json.dumps(data)
    return data

def get_current_courses():
    """call PG and retrieve all current courses provided by Dokeos"""
    # current_courses_list = []
    # pg_url = 'http://api.redhat.prepathgather.com/v1/content/'
    # response = requests.get(pg_url, headers=pg_headers)
    # response_as_json = response.json()
    # add pagination
    # current_courses_list.append(all courses from response)
    # return current_courses_list
    pass


def main():
    dw_courses = get_courses_from_dw()
    pg_courses = get_current_courses()
    new_courses = check_for_new_courses(dw_courses, pg_courses)
    if new_courses:
        for course in new_courses:
            create_pg_content_obj(course)
    else:
        pass




if __name__ == '__main__':
    from config import datawarehouse
    from config import pg_headers
    import mysql.connector
    import requests
    import json
    import datetime

# attempt to maintain a previous checked time to query against. I'm not sure if \
# that's the best idea.
    last_checked = datetime.timedelta(1)
    main()
    last_checked = datetime.now()
