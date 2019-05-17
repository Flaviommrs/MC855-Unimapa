from flask import request
from flask_restful import Resource, reqparse

from .decorators import authenticate, decode_token

from ..schemas import UserSchema
from ..models import User

from ..config import settings


class SignUpResource(Resource):
    
    @decode_token
    def post(self):
        uid = self.decoded_token['uid']
        name = self.decoded_token['name']
        email = self.decoded_token['email']
        user = User.get_or_create(id = uid, name=name, email=email)
        return UserSchema().dump(user).data, 201