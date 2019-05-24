from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from datetime import datetime
from geojson import Feature, Point, FeatureCollection

from ..schemas import MapSchema, PostSchema, SubscriptionSchema
from ..models import Map, Post, Subscription, db

from .decorators import authenticate

api_bp = Blueprint('map_api', __name__)
api = Api(api_bp)

class MapResource(Resource):
    def get(self, map_id):
        _map = Map.query.get(map_id)
        return MapSchema().dump(_map).data, 200

class MapListResource(Resource):
    def get(self):
        res = []
        for _map in Map.query.all():
            res.append(MapSchema().dump(_map).data)
        return res

    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('map_id')
        parser.add_argument('name')

        args = parser.parse_args()
        newMap = Map(args['map_id'], name=args['name'], posts=0)
        newMap.save()
        
        return MapSchema().dump(newMap).data, 201