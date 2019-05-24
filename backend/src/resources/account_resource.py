from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from .decorators import authenticate, decode_token

from ..schemas import UserSchema, MapSchema, SubscriptionSchema, PostSchema
from ..models import User, Map, Subscription, Post

from ..config import settings

api_bp = Blueprint('my_account_api', __name__)
api = Api(api_bp)

class MyAccount(Resource):

    @authenticate
    def get(self):
        return UserSchema().dump(self.user).data, 200


class MyMaps(Resource):

    @authenticate
    def get(self):
        subscriptions = self.user.subscriptions

        return [SubscriptionSchema().dump(s).data for s in subscriptions]