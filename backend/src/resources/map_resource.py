from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from datetime import datetime
from geojson import Feature, Point, FeatureCollection

from ..schemas import MapSchema, PostSchema, SubscriptionSchema
from ..models import Map, Post, Subscription, db
from ..services.notification_service import send_notification

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
        args = edit_parser.parse_args()

        for key, value in args.items():
            setattr(_map, key, value)

        db.session.commit()

        return PostSchema().dump(_map).data, 200



class MapListResource(Resource):

    @authenticate
    def get(self):
        return MapSchema().dump(Map.query.all(), many=True).data

    @authenticate
    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('name', required=True)

        args = parser.parse_args()
        newMap = Map(name=args['name'])

        db.session.add(newMap)
        db.session.commit()
        
        return MapSchema().dump(newMap).data, 201


class MapPostResource(Resource):

    @authenticate
    @get_or_404(Map)
    def get(self, _map):
        feature_collection_list = [] 
        for post in _map.posts:
            if post.point_x and post.point_y:
                feature_collection_list.append(Feature(geometry=Point((post.point_x, post.point_y))))
            else:
                feature_collection_list.append(Feature(geometry=Point((0, 0))))
        return FeatureCollection(feature_collection_list), 200

    @authenticate
    @get_or_404(Map)
    def post(self, _map):
        parser = reqparse.RequestParser()
        parser.add_argument('message')
        parser.add_argument('point_x', type=float)
        parser.add_argument('point_y', type=float)

        args = parser.parse_args()
        new_post = Post(
            user_id=self.user.id,
            post_time=datetime.utcnow(),
            map_id=map_id,
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

class MapSubscriptionResource(Resource):

    @authenticate
    @get_or_404(Map)
    def get(self, _map):
        return SubscriptionSchema().dump(Subscription.query.filter_by(map=_map), many=True).data, 200

    
    @authenticate
    @get_or_404(Map)
    def post(self, _map):
        if Subscription.query.filter_by(user=self.user, map=_map).first() != None:
            return 'User has already subscribed in this map', 400

        new_subscription = Subscription(
            user_id=self.user.id,
            subscription_time=datetime.utcnow(),
            map_id=map_id,
        )
        db.session.add(new_subscription)

        try:
            db.session.commit()
        except Exception as e:
            db.session.rollback()
            db.session.flush()
            return '', 500
        
        return SubscriptionSchema().dump(new_subscription).data, 201


api.add_resource(MapListResource, '/maps')
api.add_resource(MapResource, '/maps/<int:map_id>')
api.add_resource(MapPostResource, '/maps/<int:map_id>/posts')
api.add_resource(MapSubscriptionResource, '/maps/<int:map_id>/subscriptions')