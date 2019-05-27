from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from .decorators import authenticate, decode_token

from ..schemas import UserSchema
from ..models import User, NotificationToken, db

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

        parser = reqparse.RequestParser()
        parser.add_argument('notification_token')
        args = parser.parse_args()

        print(args['notification_token'])

        notification_token = NotificationToken(
            notification_token = args['notification_token'],
            user = user
        )

        db.session.add(notification_token)
        db.session.commit()
        
        return UserSchema().dump(user).data, 201

api.add_resource(SignUpResource, '/sign-up')