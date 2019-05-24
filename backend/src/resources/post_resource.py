from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from datetime import datetime
from geojson import Feature, Point, FeatureCollection

from ..schemas import PostSchema
from ..models import Post, User, Map, db

from .decorators import authenticate

api_bp = Blueprint('post_api', __name__)
api = Api(api_bp)

class UserPostListResource(Resource):
    def get(self, user_id):
        user = User.query.get(user_id)
        return PostSchema().dump(Post.query.filter_by(user=user), many=True).data, 200

class MapPostListResource(Resource):
    def get(self, map_id):
        posts = Post.query(map_id)
        feature_collection_list = [] 
        for post in posts:
            feature_collection_list.append(Feature(geometry=post.point))
        return FeatureCollection(feature_collection_list), 200

class PostListResource(Resource):
    def get(self):
        res = []
        for post in Post.query.all():
            res.append(PostSchema().dump(post).data)
        return res, 200

    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('map_id', type=int)
        parser.add_argument('post_id', type=int)
        parser.add_argument('username')
        parser.add_argument('message')
        parser.add_argument('pos_x', type=float)
        parser.add_argument('pos_y', type=float)

        args = parser.parse_args()
        new_post = Post(args['map_id'],
            args['post_id'],
            post_time=datetime.utcnow(),
            username=args['username'],
            message=args['message'],
            point=Point((args['pos_x'], args['pos_y'])),
            )
        new_post.save()
        
        return PostSchema().dump(new_post).data, 201