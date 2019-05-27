from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from ..schemas import UserSchema, PostSchema, SubscriptionSchema
from ..models import User, db

from .decorators import authenticate

api_bp = Blueprint('user_api', __name__)
api = Api(api_bp)

class UserResource(Resource):

    @authenticate
    def get(self, user_id):
        user = User.get_or_404(user_id)

        return UserSchema().dump(user).data, 200

    @authenticate
    def delete(self, user_id):
        user = User.get_or_404(user_id)

        db.session.delete(user)
        db.session.commit()
        return '', 204

    @authenticate
    def put(self, user_id):
        user = User.get_or_404(user_id)

        edit_parser = reqparse.RequestParser()
        edit_parser.add_argument('name', required=True)
        args = edit_parser.parse_args()

        for key, value in args.items():
            setattr(user, key, value)

        db.session.commit()

        return UserSchema().dump(user).data, 201


class UserListResource(Resource):
    @authenticate
    def get(self):
        return UserSchema().dump(User.query.all(), many=True).data, 200


class UserPostsResource(Resource):
    @authenticate
    def get(self, user_id):
        user = User.get_or_404(user_id)
        return PostSchema().dump(user.posts, many=True).data, 200


class UserSubscriptionListResource(Resource):
    @authenticate
    def get(self, user_id):
        user = User.get_or_404(user_id)
        return SubscriptionSchema().dump(user.subscriptions, many=True).data, 200


api.add_resource(UserListResource, '/users')
api.add_resource(UserResource, '/users/<string:user_id>')
api.add_resource(UserPostsResource, '/users/<string:user_id>/posts')
api.add_resource(UserSubscriptionListResource, '/users/<string:user_id>/subscriptions')