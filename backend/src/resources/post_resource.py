from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from datetime import datetime
from geojson import Feature, Point, FeatureCollection

from ..schemas import PostSchema
from ..models import Post, User, Map, db

from ..services.notification_service import send_notification

from .decorators import authenticate, owner_or_404, get_or_404

api_bp = Blueprint('post_api', __name__)
api = Api(api_bp)

class PostResource(Resource):
    
    @authenticate
    @get_or_404(Post)
    def get(self, post):
        return PostSchema().dump(post).data, 200


    @authenticate
    @owner_or_404(Post)
    def delete(self, post):
        db.session.delete(post)
        db.session.commit()
        return '', 204


    @authenticate
    @owner_or_404(Post)
    def put(self, post):
    
        edit_parser = reqparse.RequestParser()
        edit_parser.add_argument('message')
        edit_parser.add_argument('lat', type=float)
        edit_parser.add_argument('lon', type=float)
        args = edit_parser.parse_args()

        for key, value in args.items():
            if key == 'lat':
                setattr(post, 'point_x', value)
            elif key == 'lon':
                setattr(post, 'point_y', value)
            else:
                setattr(post, key, value)

        db.session.commit()

        return PostSchema().dump(post).data, 201


class PostListResource(Resource):

    @authenticate
    def get(self):
        return {'posts' : PostSchema().dump(Post.query.all(), many=True).data}, 200

    @authenticate
    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('title', required=True, help="Post title is required")
        parser.add_argument('message')
        parser.add_argument('map_id', type=int, required=True, help="Map id is required")
        parser.add_argument('lat', type=float)
        parser.add_argument('lon', type=float)

        args = parser.parse_args()
        _map = Map.get_or_404(args['map_id'])

        if _map.read_only:
            return 'The map is read only, it is not possible to create posts', 400

        if _map.user != self.user:
            subscription = Subscription.query.filter_by(user=self.user, map=_map).first()
            if subscription == None:
                return 'The user need to be subscribed to the map to create a new post', 400

        new_post = Post(
            user = self.user,
            post_time=datetime.utcnow(),
            map = _map,
            title=args['title'],
            message=args['message'],
            point_x=args['lat'], 
            point_y=args['lon'],
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