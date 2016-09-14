#!/bin/python

from datetime import date
from config   import *
import json
import time
import logging
import requests


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

    #Remove the white spaces from authorization key
    pg_headers["Authorization"] = pg_headers["Authorization"].replace("Bearer ","")
    pg_headers["Authorization"] = pg_headers["Authorization"].replace(" ","")
    pg_headers["Authorization"] = "Bearer " + pg_headers["Authorization"]
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
