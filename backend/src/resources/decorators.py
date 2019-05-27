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


def owner_or_404(model):
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            instance_id = kwargs.pop(list(kwargs)[0])
            instance = model.get_or_404(instance_id)

            if instance.has_ownership(args[0].user):
                return func(*args, instance, **kwargs)
            else:
                return 'You do not have permission to this {}'.format(model.__name__), 401
        return wrapper
    return decorator


def get_or_404(model):
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            instance_id = kwargs.pop(list(kwargs)[0])
            instance = model.get_or_404(instance_id)

            instance.has_ownership(args[0].user)
            return func(*args, instance, **kwargs)
        return wrapper
    return decorator