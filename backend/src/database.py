# -*- coding: utf-8 -*-
import os
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