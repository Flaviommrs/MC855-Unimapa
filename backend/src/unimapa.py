# -*- coding: utf-8 -*-
import os
import click

from flask import Flask
from flask_restful import Resource, Api, reqparse
from flask.cli import AppGroup
from flask.cli import with_appcontext

from .schemas import UserSchema
from .models import User
from .resources import UserResource, UserListResource, MapResource, MapListResource

import os

from flask import Flask

app = Flask(__name__)

@app.cli.command()
def create_database():
    prepare_database()

app.cli.add_command(create_database)

# Routes
api = Api(app)
api.add_resource(UserListResource, '/users')
api.add_resource(UserResource, '/users/<string:username>')
api.add_resource(MapListResource, '/maps')
api.add_resource(MapResource, '/maps/<int:map_id>')


