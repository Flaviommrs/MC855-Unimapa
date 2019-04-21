from flask_restful import Resource, reqparse

from ..schemas import UserSchema
from ..models import User

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