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

# Firebase auth configuration
cred = credentials.Certificate('./serviceAccountKey.json')
firebase_app = firebase_admin.initialize_app(cred)


def create_app(test_config=None):
    app = Flask(__name__)

    # Database configuration
    app.config['SQLALCHEMY_DATABASE_URI'] = settings.DATABASE_CONFIG
    if 'FLASK_ENV' in os.environ and os.environ['FLASK_ENV'] == 'development':
        app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/test.db'
        if settings.DATABASE_CONFIG_LOCAL:
            app.config['SQLALCHEMY_DATABASE_URI'] = settings.DATABASE_CONFIG_LOCAL
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

    if test_config:
        app.config.update(test_config)

    db.init_app(app)
    migrate = Migrate(app, db)


    # Routes
    app.register_blueprint(resources.account_bp)
    app.register_blueprint(resources.signup_bp)
    app.register_blueprint(resources.token_bp)

    app.register_blueprint(resources.post_bp)
    app.register_blueprint(resources.user_bp)
    app.register_blueprint(resources.subscription_bp)
    app.register_blueprint(resources.map_bp)

    @app.cli.command()
    def create_database():
        database.create_database(db)
        print("Database created")


    @app.cli.command()
    def mock_database():
        session = db.session
        database.mock_database_users(session, 50)
        database.mock_database_maps(session, 5)
        database.mock_database_subscriptions(session, 5)
        database.mock_database_posts(session, 20)
        
        print("Database mocked")


    @app.cli.command()
    def send_notification():
        notifications.send_notification([], "Hello", "World")
        print("Notification Sent")


    # Commands
    app.cli.add_command(mock_database)
    app.cli.add_command(create_database)
    app.cli.add_command(send_notification)

    return app

app = create_app()

if __name__ == '__main__':
    app.run()
    