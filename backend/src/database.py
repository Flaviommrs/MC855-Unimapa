# -*- coding: utf-8 -*-
from .models import User, Map, Subscription, Post

def prepare_database():
    if not User.exists():
        User.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)
    if not Map.exists():
        Map.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)
    if not Subscription.exists():
        Subscription.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)
    if not Post.exists():
        Post.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)