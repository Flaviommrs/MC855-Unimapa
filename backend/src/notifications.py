import requests
import json
from .config import settings

def send_notification(users, title, text):
    URL = 'https://fcm.googleapis.com/fcm/send'
    USERS = [settings.FCM_TEST_USER]
    FCM_KEY = settings.FCM_KEY

    data = { 
        'registration_ids' : USERS, 
        'notification' : {
            'title' : title,
            'body' : text
        } 
    }
    headers = { 'Authorization' : 'key={}'.format(FCM_KEY) , 'Content-type' : 'application/json'}
    r = requests.post(URL, headers=headers, data=json.dumps(data))