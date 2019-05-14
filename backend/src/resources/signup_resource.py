from flask import request
from flask_restful import Resource, reqparse

from .decorators import authenticate, decode_token

from ..schemas import UserSchema
from ..models import User

from ..config import settings


class SignUpResource(Resource):

    @authenticate
    def get(self):
        return UserSchema().dump(self.user).data, 200

    @decode_token
    def post(self):
        uid = self.decoded_token['uid']
        user = User.get_or_create(uid = uid, name=uid, email=uid, username=uid)
        return UserSchema().dump(user).data, 201