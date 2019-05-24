from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from .decorators import authenticate, decode_token

from ..schemas import UserSchema
from ..models import User

from ..config import settings

api_bp = Blueprint('signup_api', __name__)
api = Api(api_bp)

class SignUpResource(Resource):
    
    @decode_token
    def post(self):
        uid = self.decoded_token['uid']
        name = self.decoded_token['name']
        email = self.decoded_token['email']
        user = User.get_or_create(User, id=uid, name=name, email=email)
        return UserSchema().dump(user).data, 201