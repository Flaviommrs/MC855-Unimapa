# -*- coding: utf-8 -*-
from flask import Flask
from flask_restful import Resource, Api, reqparse

from src.schemas import UserSchema
from src.database import USERS
from src.models import User


app = Flask(__name__)
api = Api(app)

class HelloWorld(Resource):
    def get(self):
        return {'hello': 'world'}

class UserResource(Resource):
    def get(self, user_id):
        return USERS[user_id], 200


class UserListResource(Resource):
    def get(self):
        return USERS

    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('name')

        args = parser.parse_args()
        newUser = User(args['name'])
        print(UserSchema().dump(newUser).data)
        USERS[len(USERS)] = UserSchema().dump(newUser).data

        return USERS[len(USERS) - 1], 201

api.add_resource(HelloWorld, '/')
api.add_resource(UserListResource, '/users')
api.add_resource(UserResource, '/users/<int:user_id>')

if __name__ == '__main__':
    app.run(debug=True)
