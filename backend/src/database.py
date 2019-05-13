# -*- coding: utf-8 -*-
import os
import random

from datetime import datetime
from geojson import Point

from .models import User, Map, Subscription, Post

def mock_database(db):
    if 'FLASK_ENV' in os.environ and os.environ['FLASK_ENV'] == 'development':
        users = []
        maps = []

        for i in range(50):
            new_user = User(
                uid=i,
                username="Username {}".format(i),
                name="Nome {}".format(i),
                email='username{}@gmail.com'.format(i)
            )
            users.append(new_user)
            db.session.add(new_user)
        
        for i in range(5):
            new_map = Map(name="Mapa {}".format(i))
            maps.append(new_map)
            db.session.add(new_user)

        for i in range(10):
            new_subscription = Subscription(
                user = users[i],
                map = maps[i % 5],
                subscription_time = datetime.utcnow()
            )
            db.session.add(new_subscription)

        for i in range(20):
            c = datetime.utcnow()
            new_post = Post(
                post_time = datetime.utcnow(), 
                message = "Post {}".format(i),
                point_x = random.uniform(-47.1,-47.0),
                point_y = random.uniform(-22.9,-22.8),
                user = users[i % 5],
                map = maps[i % 3]
            )
            db.session.add(new_post)

        db.session.commit()