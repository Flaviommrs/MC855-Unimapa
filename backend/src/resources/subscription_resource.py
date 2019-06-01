from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from ..schemas import SubscriptionSchema
from ..models import Subscription, User, Map, db

from datetime import datetime

from .decorators import authenticate, owner_or_404, get_or_404

api_bp = Blueprint('subscription_api', __name__)
api = Api(api_bp)

class SubscriptionResource(Resource):

    @authenticate
    @get_or_404(Subscription)
    def get(self, subscription):
        return SubscriptionSchema().dump(subscription).data, 200

    @authenticate
    @owner_or_404(Subscription)
    def delete(self, subscription):
        db.session.delete(subscription)
        db.session.commit()
        return '', 204


class SubscriptionListResource(Resource):
    @authenticate
    def get(self):
        return SubscriptionSchema().dump(Subscription.query.all(), many=True).data, 200

    @authenticate
    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('map_id', type=int, required=True)

        args = parser.parse_args()

        _map = Map.get_or_404(args['map_id'])

        if Subscription.query.filter_by(user=self.user, map=_map).first() != None:
            return 'User has already subscribed in this map', 400

        new_subscription = Subscription(
            map = _map,
            user = self.user,
            subscription_time=datetime.now()
        )
        
        db.session.add(new_subscription)

        try:
            db.session.commit()
        except Exception as e:
            db.session.rollback()
            db.session.flush()
            return '', 500
        
        return SubscriptionSchema().dump(new_subscription).data, 201

api.add_resource(SubscriptionListResource, '/subscriptions')
api.add_resource(SubscriptionResource, '/subscriptions/<int:subscription_id>')