# integrate DW and PG - pseudo-code
from configparser import SafeConfigParser
import mysql.connector
import requests


def get_new_completions(creds):
    """connect to datawarehouse and retrieve new completions"""
    connection = mysql.connector.connect(**creds)
    cursor = connection.cursor()
    # Real queries are being tested in test.py
    query = ("SELECT * FROM Students WHERE email like '%redhat.com%'")
    cursor.execute(query)

def main():
    from config import datawarehouse
    new_completions = get_new_completions(datawarehouse)

    # This next bit is pseudo-code
    if new_completions:
        pathgather = HTTP(pg_key, url)
        for comp in new_completions:
            user = comp.user
            pathgather.user.mark_complete()
        return 200

    else:
        print("nothing")

main()
