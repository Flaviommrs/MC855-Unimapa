from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from datetime import datetime
from geojson import Feature, Point, FeatureCollection

from ..schemas import MapSchema, PostSchema, SubscriptionSchema
from ..models import Map, Post, Subscription, db
from ..services.notification_service import send_notification
from ..services.geojson_helpers import posts_to_geojson

from .decorators import authenticate, get_or_404, owner_or_404

api_bp = Blueprint('map_api', __name__)
api = Api(api_bp)


class MapResource(Resource):

    @authenticate
    @get_or_404(Map)
    def get(self, _map):
        return MapSchema().dump(_map).data, 200

    @authenticate
    @owner_or_404(Map)
    def delete(self, _map):
        for post in _map.posts:
            db.session.delete(post)
        for sub in _map.subscriptions:
            db.session.delete(sub)
        db.session.delete(_map)
        db.session.commit()
        return '', 204

    @authenticate
    @owner_or_404(Map)
    def put(self, _map):
        edit_parser = reqparse.RequestParser()
        edit_parser.add_argument('name', required=True)
        edit_parser.add_argument('read_only', type=bool, required=False)
        args = edit_parser.parse_args()

        for key, value in args.items():
            if value != None:
                setattr(_map, key, value)
        db.session.add(_map)
        db.session.commit()

        return MapSchema().dump(_map).data, 200



class MapListResource(Resource):

    @authenticate
    def get(self):
        return {'maps' : MapSchema().dump(Map.query.all(), many=True).data}, 200

    @authenticate
    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('name', required=True)
        parser.add_argument('read_only', type=bool, required=False)

        args = parser.parse_args()
        
        newMap = Map(
            name=args['name'],
            user=self.user,
            read_only=args['read_only']
        )

        db.session.add(newMap)
        db.session.commit()
        
        return MapSchema().dump(newMap).data, 201


class MapPostResource(Resource):

    # @authenticate
    @get_or_404(Map)
    def get(self, _map):
        page = request.args.get('page', None)
        if page:
            page = int(page)
        per_page = request.args.get('per_page', (None if _map.read_only else 10))
        if per_page:
            per_page = int(per_page)

        posts = Post.query.filter_by(map=_map).order_by(Post.post_time.desc()).paginate(page=page, per_page=per_page).items
        feature_collection_list = posts_to_geojson(posts)

        return FeatureCollection(feature_collection_list), 200

    @authenticate
    @get_or_404(Map)
    def post(self, _map):
        if _map.read_only:
            return 'The map is read only, it is not possible to create posts', 400

        if _map.user != self.user:
            subscription = Subscription.query.filter_by(user=self.user, map=_map).first()
            if subscription == None:
                return 'The user need to be subscribed to the map to create a new post', 400

        parser = reqparse.RequestParser()
        parser.add_argument('title', required=True, help="Post title is required")
        parser.add_argument('message')
        parser.add_argument('lat', type=float)
        parser.add_argument('lon', type=float)

        args = parser.parse_args()
        new_post = Post(
            user_id=self.user.id,
            post_time=datetime.utcnow(),
            map=_map,
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

class MapSubscriptionResource(Resource):

    @authenticate
    @get_or_404(Map)
    def get(self, _map):
        return {'subscriptions' : SubscriptionSchema().dump(Subscription.query.filter_by(map=_map), many=True).data}, 200

    
    @authenticate
    @get_or_404(Map)
    def post(self, _map):
        if Subscription.query.filter_by(user=self.user, map=_map).first() != None:
            return 'User has already subscribed in this map', 400

        new_subscription = Subscription(
            user_id=self.user.id,
            subscription_time=datetime.utcnow(),
            map=_map,
        )
        db.session.add(new_subscription)

        try:
            db.session.commit()
        except Exception as e:
            db.session.rollback()
            db.session.flush()
            return '', 500
        
        return SubscriptionSchema().dump(new_subscription).data, 201

    @authenticate
    @get_or_404(Map)
    def delete(self, _map):
        subscription = Subscription.query.filter_by(user=self.user, map=_map).first()
        if subscription == None:
            return 'User is not subscribed to this map', 400

        db.session.delete(subscription)
        db.session.commit()

        return '', 204


api.add_resource(MapListResource, '/maps')
api.add_resource(MapResource, '/maps/<int:map_id>')
api.add_resource(MapPostResource, '/maps/<int:map_id>/posts')
api.add_resource(MapSubscriptionResource, '/maps/<int:map_id>/subscriptions')