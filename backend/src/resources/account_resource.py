from flask import request
from flask_restful import Resource, reqparse

from .decorators import authenticate, decode_token

from ..schemas import UserSchema, MapSchema, SubscriptionSchema, PostSchema
from ..models import User, Map, Subscription, Post

from ..config import settings


class MyAccount(Resource):

    @authenticate
    def get(self):
        return UserSchema().dump(self.user).data, 200


class MyMaps(Resource):

    @authenticate
    def get(self):
        subscriptions = self.user.subscriptions

        return [SubscriptionSchema().dump(s).data for s in subscriptions]