#!/bin/python

def get_time_isoformat():
    #--No of days to go back--
    time_diff = 1
    #-------------------------
    nowdate = datetime.now()- timedelta(days=time_diff)
    utcdate = datetime.utcnow()- timedelta(days=time_diff)
    delta = nowdate - utcdate 
    hh,mm = divmod((delta.days * 24*60*60 + delta.seconds + 30) // 60, 60)
    return "%s%+03d:%02d" % (nowdate.isoformat(), hh, mm)


def create_url_with_params(**kwargs):
    url = kwargs["url"]
    #%5B is '[' and %5D is ']'
    params = "?q%5Bcreated_at%5D%5Bgt%5D={}".format(quote(kwargs["time"]))
    pagination_params="&from={}".format(kwargs["fromuser"])
    if kwargs["fromuser"] is None:
        return url + params
    else:
        return url + params + pagination_params

def send_api_request(url, headers):
    logging.info("Url = %s " % (url))
    req = requests.get(url, headers=headers)
    if req.status_code == 404:
        logging.error("%s :API GET request error" % (req.status_code))
    else:
        logging.info("%s : API GET request success" % (req.status_code))
    return json.loads(req.text)


def scrape_user_emails(jsondata):
    emails = []
    users = jsondata["results"]
    for user in users:
        emails.append(user["email"].encode('utf-8'))

    return emails
        

def get_new_users():
    iso_time = get_time_isoformat()
    pg_url = 'http://{}/v1/users'.format(pathgather_url)

    #Remove the white spaces from authorization key
    pg_headers["Authorization"] = pg_headers["Authorization"].replace("Bearer ","")
    pg_headers["Authorization"] = pg_headers["Authorization"].replace(" ","")
    pg_headers["Authorization"] = "Bearer " + pg_headers["Authorization"]

    user_emails = []
    nextuserid = None
    while True:
        url = create_url_with_params(url=pg_url, time=iso_time, fromuser=nextuserid)
        jsondata = send_api_request(url, pg_headers)
        scraped_emails = scrape_user_emails(jsondata)
        user_emails += scraped_emails
        nextuserid = jsondata["next"]
        if nextuserid is None:
            break

    logging.info("Found %s new users" % (len(user_emails)))
    return user_emails

def connect_db(creds, mysql_con):
    try:
        mysql_con["connection"] = mysql.connector.connect(**creds)
    except Exception, e:
        logging.exception(e)

def db_operations(text):
    logging.info(text)

    logging.info("Find all new users in PG in the past 24 hours")
    fresh_user_email_ids=get_new_users()
    if len(fresh_user_email_ids) == 0:
        logging.info("Terminating process")
        return

    mysql_con = {}
    connect_db(datawarehouse, mysql_con)
    cursor = mysql_con["connection"].cursor()    
    logging.info("Fetch student ids of all new users from Students Table")
    query = """SELECT StudentID, Email FROM Students 
            WHERE Email IN {}""".format(str(tuple(fresh_user_email_ids)))
    try:
        cursor.execute(query)
    except Exception, e:
        logging.exception(e)
    finally:
        fresh_users = { }
        for record in cursor:
            fresh_users[record[0]]=record[1]

    logging.info("Check in StudentCourses Table for course completions")
    cursor = mysql_con["connection"].cursor()
    query = """SELECT CourseID, AssessmentDate, StudentID 
            FROM `StudentCourses` WHERE `StudentID` IN {} 
            AND `AssessmentResult`='Pass' """.format(str(tuple(fresh_users.keys())))
    try:
        cursor.execute(query)
    except Exception, e:
        logging.exception(e)
    finally:
        completed_users = cursor.fetchall()
        logging.info("There are %s course completions" % len(completed_users))
        for record in completed_users:
            content_obj = []
            content_obj.append(record[1])
            content_obj.append(fresh_users[record[2]])       
            content_obj.append(record[0])
            jsondata=create_pg_user_content_obj(content_obj)
            mark_complete(jsondata)

    mysql_con["connection"].close()
    logging.info("DB connection closed")
    logging.info("End of Process")

def prompt_user():
    create_log_file()
    logging.info("Ask user permission to continue")

    user_choice = input("Mark course completion for new users every 24 hours? y or n ").lower()
    if user_choice == 'y':
        present_time = datetime.now()
        schedule_time = "%s:%s" % (present_time.hour, (present_time.minute+1))
        logging.info("Scheduler will run at " + schedule_time + " every day")
        schedule.every().day.at(schedule_time).do(db_operations,'Process started')        
        while True:
            schedule.run_pending()
            time.sleep(1)
    elif user_choice == 'n':
        logging.info("User aborted process")
    else:
        return prompt_user()

if __name__ == '__main__':
    from config   import *
    from common   import *
    from datetime import date, datetime, timedelta
    from dateutil import parser
    from urllib import quote
    import mysql.connector
    import requests
    import json
    import schedule
    import time

    prompt_user()



