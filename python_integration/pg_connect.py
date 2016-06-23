def get_pg_users():
    """Return list of user ids"""
    pg_url = 'http://api.redhat.prepathgather.com/v1/users/'
    response = requests.get(pg_url, headers=pg_headers)
    response_as_json = response.json()
    user_list = response_as_json['results']
    ids_list = [user['id'] for user in user_list]
    return ids_list

def get_pg_user_content():
    """returns list of incomplete user_content objects"""
    pg_url = 'http://api.redhat.prepathgather.com/v1/user_content/'
    response = requests.get(pg_url, headers=pg_headers)
    response_as_json = response.json()
    content_objects = response_as_json['results']
    # search results for incompletes
    completed_at_list = [obj for obj in content_objects if not obj['completed_at']]
    return completed_at_list

def set_complete():
    """gets incomplete user_content objects and sets the 'completed_at' field \
       with a timestamp string"""
    get_pg_user_content()
    for obj in completed_at_list:
        # make json with completed_at = 'now'
        data = {
            "content_id": obj['content_id'],
            "user_email": obj['user_email'],
            "completed_at": 'now'
          }
        # figure this syntax out/post with requests
        requests.post(pg_url, obj, headers=headers)
    return 200

def main():
    get_pg_users()
    set_complete()


if __name__ == '__main__':
    import requests
    import json
    import datetime
    from config import pg_headers

    main()
