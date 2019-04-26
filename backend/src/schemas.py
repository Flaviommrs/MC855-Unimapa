from marshmallow import Schema, fields, post_load

from .models import User, Map, Subscription, Post

import geojson


class UserSchema(Schema):
    username = fields.Str()
    name = fields.Str()

    @post_load
    def make_user(self, data):
        return User(**data)


class MapSchema(Schema):
    map_id = fields.Integer()
    name = fields.Str()
    posts = fields.Integer()

    @post_load
    def make_map(self, data):
        return Map(**data)


class SubscriptionSchema(Schema):
    username = fields.Str()
    map_id = fields.Integer()
    subscription_date = fields.DateTime()

    @post_load
    def make_subscription(self, data):
        return Subscription(**data)


class PostSchema(Schema):
    post_id = fields.Integer()
    map_id = fields.Integer()
    post_time = fields.DateTime()
    message = fields.String()
    username = fields.String()
    point = fields.Dict()

    @post_load
    def make_post(self, data):
        return Post(**data)