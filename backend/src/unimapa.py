# -*- coding: utf-8 -*-
import os
import click
import firebase_admin

from flask import Flask, request, jsonify
from flask.cli import AppGroup, with_appcontext
from flask_restful import Resource, Api, reqparse
from flask_migrate import Migrate

from firebase_admin import credentials

from .config import settings
from .models import db

from . import resources
from . import database
from . import notifications

def create_app():
    app = Flask(__name__)

    # Database configuration
    if 'FLASK_ENV' in os.environ and os.environ['FLASK_ENV'] == 'development':
        app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/test.db'
    else:
        app.config['SQLALCHEMY_DATABASE_URI'] = settings.DATABASE_CONFIG

    db.init_app(app)
    migrate = Migrate(app, db)

    # Firebase auth configuration
    cred = credentials.Certificate('./serviceAccountKey.json')
    firebase_app = firebase_admin.initialize_app(cred)

    return app

# App creation
app = create_app()

@app.cli.command()
def create_database():
    db.drop_all()
    db.create_all()
    db.session.commit()
    print("Database created")

@app.cli.command()
def mock_database():
    database.mock_database(db)
    print("Database mocked")

@app.cli.command()
def send_notification():
    notifications.send_notification([], "Hello", "World")
    print("Notification Sent")

# Commands
app.cli.add_command(mock_database)
app.cli.add_command(create_database)
app.cli.add_command(send_notification)

# Routes
api = Api(app)
api.add_resource(resources.SignUpResource, '/sign-up')

api.add_resource(resources.UserListResource, '/users')
api.add_resource(resources.UserResource, '/users/<int:user_id>')

api.add_resource(resources.UserSubscriptionListResource, '/users/<int:user_id>/subscriptions')
api.add_resource(resources.UserPostListResource, '/users/<int:user_id>/posts')

api.add_resource(resources.MapListResource, '/maps')
api.add_resource(resources.MapResource, '/maps/<int:map_id>')
api.add_resource(resources.MapSubscriptionListResource, '/maps/<int:map_id>/subscriptions')
api.add_resource(resources.MapPostListResource, '/maps/<int:map_id>/posts')

api.add_resource(resources.SubscriptionListResource, '/subscriptions')
api.add_resource(resources.SubscriptionResource, '/subscriptions/<int:subscription_id>')

api.add_resource(resources.PostListResource, '/posts')

api.add_resource(resources.TokenResource, '/token/<string:uid>')