from marshmallow import Schema, fields, post_load

from src.models import User, Map, Position, Post

class UserSchema(Schema):
    name = fields.Str()

    @post_load
    def make_user(self, data):
        return User(**data)

class MapSchema(Schema):
    name = fields.Str()

class PositionSchema(Schema):
    x = fields.Float()
    y = fields.Float()