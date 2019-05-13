from flask import request
from flask_restful import Resource, reqparse

from .decorators import authenticate

from ..schemas import UserSchema

from ..config import settings


class SignUpResource(Resource):

    @authenticate
    def get(self):
        return UserSchema().dump(self.decoded_token.user).data, 200