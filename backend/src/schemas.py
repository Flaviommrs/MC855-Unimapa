from marshmallow import Schema, fields, post_load

from .models import User, Map, Subscription, Post

import geojson


class UserSchema(Schema):
    id = fields.Str()
    email = fields.Str()
    name = fields.Str()

    @post_load
    def make_user(self, data):
        return User(**data)


class MapSchema(Schema):
    id = fields.Integer()
    name = fields.Str()
    read_only = fields.Boolean()
    
    owner = fields.Nested(UserSchema, attribute="user")

    @post_load
    def make_map(self, data):
        return Map(**data)

class PostSchema(Schema):
    id = fields.Integer()

    user = fields.Nested(UserSchema)
    map = fields.Nested(MapSchema)

    post_time = fields.DateTime()
    message = fields.String()
    point_x = fields.Float()
    point_y = fields.Float()

    @post_load
    def make_post(self, data):
        return Post(**data)

class SubscriptionSchema(Schema):
    id = fields.Integer()
    
    user = fields.Nested(UserSchema)
    map = fields.Nested(MapSchema)
    subscription_date = fields.DateTime()

    @post_load
    def make_subscription(self, data):
        return Subscription(**data)