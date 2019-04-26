# -*- coding: utf-8 -*-
import os
import random

from datetime import datetime
from geojson import Point

from .models import User, Map, Subscription, Post

def create_database():
    if not User.exists():
        User.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)
    if not Map.exists():
        Map.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)
    if not Subscription.exists():
        Subscription.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)
    if not Post.exists():
        Post.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)

def mock_database():
    if 'FLASK_ENV' in os.environ and os.environ['FLASK_ENV'] == 'development':
        for i in range(50):
            new_user = User("Username {}".format(i), name="Nome {}".format(i))
            new_user.save()
        
        for i in range(5):
            new_map = Map(i, name="Mapa {}".format(i))
            new_map.save()

        for i in range(10):
            new_subscription = Subscription(i % 5, "Username {}".format(i), subscription_time=datetime.utcnow())
            new_subscription.save()

        for i in range(10):
            c = datetime.utcnow()
            microseconds = (c.day * 24 * 60 * 60 + c.second) * 1000000 + c.microsecond
            new_post = Post(i % 3, microseconds,
                post_time=datetime.utcnow(), 
                message="Post {}".format(i),
                point=Point((random.uniform(-47.1,-47.0), random.uniform(-22.9,-22.8))),
                username="Username {}".format(i))
            new_post.save()