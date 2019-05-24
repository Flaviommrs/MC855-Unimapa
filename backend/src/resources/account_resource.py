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


class MySubscriptions(Resource):

    @authenticate
    def get(self):
        subscriptions = self.user.subscriptions

        return [SubscriptionSchema().dump(sub).data for sub in subscriptions]

class MyPosts(Resource):
    
    @authenticate
    def get(self):
        posts = self.user.posts

        return [PostSchema().dump(post).data for post in posts]

api.add_resource(MyAccount, '/my_account')
api.add_resource(MySubscriptions, '/my_subscriptions')
api.add_resource(MyPosts, '/my_posts')