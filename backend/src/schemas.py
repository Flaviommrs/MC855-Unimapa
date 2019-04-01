from marshmallow import Schema, fields, post_load

from src.models import User, Map, Subscription, Post

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
    map_id = fields.Integer()
    post_time = fields.DateTime()
    message = fields.String()
    username = fields.String()
    pos_x = fields.Integer()
    pos_y = fields.Integer()

    @post_load
    def make_post(self, data):
        return Post(**data)