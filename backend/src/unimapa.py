# -*- coding: utf-8 -*-
import os
import click

from flask import Flask
from flask_restful import Resource, Api, reqparse
from flask.cli import AppGroup

from .schemas import UserSchema
from .models import User
from flask.cli import with_appcontext

import os

from flask import Flask

class HelloWorld(Resource):
    def get(self):
        return {'hello': 'world'}

class UserResource(Resource):
    def get(self, username):
        user = User.get(username)
        return UserSchema().dump(user).data, 200

class UserListResource(Resource):
    def get(self):
        res = []
        for u in User.scan():
            res.append(UserSchema().dump(u).data)
        return res

    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('username')
        parser.add_argument('name')

        args = parser.parse_args()
        newUser = User(args['username'], name=args['name'])
        newUser.save()
        
        return UserSchema().dump(newUser).data, 201


app = Flask(__name__)

@app.cli.command()
def create_database():
    prepare_database()

app.cli.add_command(create_database)

api = Api(app)

api.add_resource(HelloWorld, '/')
api.add_resource(UserListResource, '/users')
api.add_resource(UserResource, '/users/<string:username>')


