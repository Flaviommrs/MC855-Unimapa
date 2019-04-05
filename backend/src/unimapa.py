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
                        SubscriptionResource, SubscriptionListResource, UserSubscriptionResource)
from . import database

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

app.cli.add_command(mock_database)
app.cli.add_command(create_database)

# Routes
api = Api(app)
api.add_resource(UserListResource, '/users')
api.add_resource(UserResource, '/users/<string:username>')
api.add_resource(UserSubscriptionResource, '/users/<string:username>/subscriptions')
api.add_resource(MapListResource, '/maps')
api.add_resource(MapResource, '/maps/<int:map_id>')
api.add_resource(SubscriptionListResource, '/subscriptions')
api.add_resource(SubscriptionResource, '/subscriptions/<int:map_id>')


