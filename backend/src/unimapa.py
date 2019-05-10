# -*- coding: utf-8 -*-
import os
import click

import google.auth.transport.requests
import google.oauth2.id_token

from flask import Flask, request, jsonify
from flask_restful import Resource, Api, reqparse
from flask.cli import AppGroup, with_appcontext


from .config import settings
from .schemas import UserSchema
from .models import User, db
from .resources import (UserResource, UserListResource, 
                        MapResource, MapListResource,
                        UserSubscriptionListResource, SubscriptionListResource, 
                        SubscriptionResource, MapSubscriptionListResource, 
                        PostListResource, UserPostListResource, MapPostListResource)
from . import database
from . import notifications

def create_app():
    app = Flask(__name__)

    # Database configuration
    if 'FLASK_ENV' in os.environ and os.environ['FLASK_ENV'] == 'development':
        app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/test.db
    else:
        app.config['SQLALCHEMY_DATABASE_URI'] = settings.DATABASE_CONFIG

    db.init_app(app)

    return app

# App creation
app = create_app()

@app.cli.command()
def create_database():
    db.create_all()
    print("Database created")

@app.cli.command()
def mock_database():
    database.mock_database(db)
    print("Database mocked")

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
api.add_resource(UserResource, '/users/<int:user_id>')

api.add_resource(UserSubscriptionListResource, '/users/<int:user_id>/subscriptions')
api.add_resource(UserPostListResource, '/users/<int:user_id>/posts')

api.add_resource(MapListResource, '/maps')
api.add_resource(MapResource, '/maps/<int:map_id>')
api.add_resource(MapSubscriptionListResource, '/maps/<int:map_id>/subscriptions')
api.add_resource(MapPostListResource, '/maps/<int:map_id>/posts')

api.add_resource(SubscriptionListResource, '/subscriptions')
api.add_resource(SubscriptionResource, '/subscriptions/<int:subscription_id>')

api.add_resource(PostListResource, '/posts')


