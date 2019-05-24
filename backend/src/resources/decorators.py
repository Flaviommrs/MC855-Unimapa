import logging 

from firebase_admin import auth

from flask import request
from functools import wraps

from ..models import User

def authenticate(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        auth_header = request.headers.get('Authorization')

        if auth_header:
            try:
                id_token = auth_header.split(" ")[1]

                decoded_token = auth.verify_id_token(id_token)
                uid = decoded_token['uid']
                user = User.query.filter_by(id=uid).first()
            except Exception as e:
                logging.exception("message")
                return 'Unauthorized', 401
                
            if user:
                args[0].user = user
                args[0].decoded_token = decoded_token
                return func(*args, **kwargs)
        return 'Unauthorized', 401 
    return wrapper


def decode_token(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        auth_header = request.headers.get('Authorization')

        if auth_header:
            try:
                id_token = auth_header.split(" ")[1]

                decoded_token = auth.verify_id_token(id_token)
            except Exception as e:
                logging.exception("message")
                return 'Unauthorized', 401
                
            if decoded_token:
                args[0].decoded_token = decoded_token
                return func(*args, **kwargs)
            
        return 'Unauthorized', 401 
    return wrapper
