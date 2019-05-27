from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from datetime import datetime
from geojson import Feature, Point, FeatureCollection

from ..schemas import PostSchema
from ..models import Post, User, Map, db

from ..services.notification_service import send_notification

from .decorators import authenticate

api_bp = Blueprint('post_api', __name__)
api = Api(api_bp)

class PostResource(Resource):
    
    @authenticate
    def get(self, post_id):
        post = Post.query.get_or_404(post_id, "Post with this id does not exist")

        return PostSchema().dump(post).data, 200

    @authenticate
    def delete(self, post_id):
        post = Post.query.get_or_404(post_id, "Post with this id does not exist")

        db.session.delete(post)
        db.session.commit()
        return '', 204

    @authenticate
    def put(self, post_id):
        post = Post.query.get_or_404(post_id, "Post with this id does not exist")

        edit_parser = reqparse.RequestParser()
        edit_parser.add_argument('message')
        edit_parser.add_argument('point_x', type=float)
        edit_parser.add_argument('point_y', type=float)
        args = edit_parser.parse_args()

        for key, value in args.items():
            setattr(post, key, value)

        db.session.commit()

        return PostSchema().dump(post).data, 201


class PostListResource(Resource):

    @authenticate
    def get(self):
        return PostSchema().dump(Post.query.all(), many=True).data, 200

    @authenticate
    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('message')
        parser.add_argument('map_id', type=int, required=True, help="Map id is required")
        parser.add_argument('point_x', type=float)
        parser.add_argument('point_y', type=float)

        args = parser.parse_args()
        _map = Map.query.get_or_404(args['map_id'], "Map with this id does not exist")

        new_post = Post(
            user = self.user,
            post_time=datetime.utcnow(),
            map = _map,
            message=args['message'],
            point_x=args['point_x'], 
            point_y=args['point_y'],
        )
        db.session.add(new_post)
        send_notification(_map, new_post)

        try:
            db.session.commit()
        except Exception as e:
            db.session.rollback()
            db.session.flush()
            return '', 500
        
        return PostSchema().dump(new_post).data, 201

api.add_resource(PostResource, '/posts/<int:post_id>')
api.add_resource(PostListResource, '/posts')