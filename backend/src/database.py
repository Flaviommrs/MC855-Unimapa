# -*- coding: utf-8 -*-
import os
import random
import string

from datetime import datetime
from geojson import Point

from .models import User, Map, Subscription, Post
from .config import settings

from functools import wraps

def random_email():
    return '{}@{}.com'.format(
        ''.join(random.choices(string.ascii_lowercase + string.digits, k=5)),
        ''.join(random.choices(string.ascii_lowercase + string.digits, k=5))
    )

def random_id():
    return ''.join(random.choices(string.ascii_lowercase + string.digits + string.ascii_uppercase, k=10))


def development_or_test(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        if 'FLASK_ENV' in os.environ and os.environ['FLASK_ENV'] == 'development':
            return func(*args, **kwargs)
        return False
    return wrapper


@development_or_test
def create_database(db):
    db.drop_all()
    db.create_all()
    db.session.commit()

@development_or_test
def mock_database(db):
    new_user = User(
        id=settings.FIREBASE_UID,
        name="Nome {}".format(random_id()),
        email=random_email()
    )
    db.session.add(new_user)

    session = db.session
    mock_database_users(session, 50)
    mock_database_maps(session, 5)
    mock_database_subscriptions(session, 5)
    mock_database_posts(session, 20)

    db.session.commit()

@development_or_test
def mock_database_users(session, users_qty = 0):
    for i in range(users_qty):
        new_user = User(
            id=random_id(),
            name="Nome {}".format(random_id()),
            email=random_email()
        )
        session.add(new_user)
    
    session.commit()

@development_or_test
def mock_database_maps(session, maps_qty = 0):
    users = User.query.all()
    for i in range(maps_qty):
        new_map = Map(
            name="Mapa {}".format(i),
            user = users[random.randint(0, len(users) - 1)],
        )
        session.add(new_map)

    session.commit()


@development_or_test
def mock_database_subscriptions(session, subscriptions_qty = 0):
    users = User.query.all()
    maps = Map.query.all()
    
    for i in range(subscriptions_qty):
        new_subscription = Subscription(
            user = users[random.randint(0, len(users) - 1)],
            map = maps[random.randint(0, len(maps) - 1)],
            subscription_time = datetime.utcnow()
        )
        session.add(new_subscription)

    session.commit()


@development_or_test
def mock_database_posts(session, posts_qty = 0):
    users = User.query.all()
    maps = Map.query.all()

    for i in range(posts_qty):
        c = datetime.utcnow()
        new_post = Post(
            post_time = datetime.utcnow(), 
            message = "Post {}".format(i),
            point_x = random.uniform(-47.1,-47.0),
            point_y = random.uniform(-22.9,-22.8),
            user = users[random.randint(0, len(users) - 1)],
            map = maps[random.randint(0, len(maps) - 1)]
        )
        session.add(new_post)

    session.commit()