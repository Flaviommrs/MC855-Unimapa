# -*- coding: utf-8 -*-
import os
import click

from flask import Flask
from flask_restful import Resource, Api, reqparse
from flask.cli import AppGroup
from flask.cli import with_appcontext

from .schemas import UserSchema
from .models import User
from .resources import (UserResource, UserListResource, 
                        MapResource, MapListResource,
                        UserSubscriptionListResource, SubscriptionListResource, 
                        MapSubscriptionResource, MapSubscriptionListResource, 
                        PostListResource, UserPostListResource, MapPostListResource)
from . import database
from . import notifications

# App creation
app = Flask(__name__)

# Command to run 
@app.cli.command()
def create_database():
    database.create_database()
    print("Database created")

# Command to run 
@app.cli.command()
def mock_database():
    database.mock_database()
    print("Database mocked")

# Command to run 
@app.cli.command()
def send_notification():
    notifications.send_notification([], "Hello", "World")
    print("Notification Sent")

app.cli.add_command(mock_database)
app.cli.add_command(create_database)
app.cli.add_command(send_notification)

# Routes
api = Api(app)
api.add_resource(UserListResource, '/users')
api.add_resource(UserResource, '/users/<string:username>')

api.add_resource(UserSubscriptionListResource, '/users/<string:username>/subscriptions')
api.add_resource(UserPostListResource, '/users/<string:username>/posts')

api.add_resource(MapListResource, '/maps')
api.add_resource(MapResource, '/maps/<int:map_id>')
api.add_resource(MapSubscriptionListResource, '/maps/<int:map_id>/subscriptions')
api.add_resource(MapSubscriptionResource, '/maps/<int:map_id>/subscriptions/<string:username>')
api.add_resource(MapPostListResource, '/maps/<int:map_id>/posts')

api.add_resource(SubscriptionListResource, '/subscriptions')

api.add_resource(PostListResource, '/posts')


