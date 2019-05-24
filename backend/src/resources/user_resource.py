from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from ..schemas import UserSchema
from ..models import User

api_bp = Blueprint('user_api', __name__)
api = Api(api_bp)

class UserResource(Resource):
    def get(self, user_id):
        user = User.query.get(user_id)
        return UserSchema().dump(user).data, 200

class UserListResource(Resource):
    def get(self):
        res = []
        for u in User.query.all():
            res.append(UserSchema().dump(u).data)
        return res