import requests
import json
import os

from ..models import Map, User, NotificationToken, Post, Subscription
from ..config import settings

def send_notification(_map, post):
    subscriptions = _map.subscriptions
    users = [sub.user for sub in subscriptions]
    user_tokens = [user.notification_tokens for user in users if user != post.user]
    tokens = []
    for user_token in user_tokens:
        tokens += user_token
    tokens = [token.notification_token for token in tokens]

    if 'FLASK_ENV' in os.environ and os.environ['FLASK_ENV'] == 'development':
        print('Send local notification to:')
        for token in tokens:
            print(token)

    URL = 'https://fcm.googleapis.com/fcm/send'
    FCM_KEY = settings.FCM_KEY

    data = { 
        'registration_ids' : tokens, 
        'notification' : {
            'title' : 'Nova postagem no mapa {}'.format(_map.name),
            'body' : post.message
        } 
    }

    headers = { 'Authorization' : 'key={}'.format(FCM_KEY) , 'Content-type' : 'application/json'}
    r = requests.post(URL, headers=headers, data=json.dumps(data))