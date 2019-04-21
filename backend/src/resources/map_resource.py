from flask_restful import Resource, reqparse

from ..schemas import MapSchema
from ..models import Map

class MapResource(Resource):
    def get(self, map_id):
        _map = Map.get(map_id)
        return MapSchema().dump(_map).data, 200

class MapListResource(Resource):
    def get(self):
        res = []
        for _map in Map.scan():
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