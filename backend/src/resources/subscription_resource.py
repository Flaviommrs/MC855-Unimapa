from flask_restful import Resource, reqparse


from ..schemas import SubscriptionSchema
from ..models import Subscription, User, Map

from datetime import datetime

class UserSubscriptionListResource(Resource):
    def get(self, user_id):
        user = User.query.get(user_id)
        subscriptions = Subscription.query.filter_by(user=user)
        return SubscriptionSchema().dump(subscriptions, many=True).data, 200


class MapSubscriptionListResource(Resource):
    def get(self, map_id):
        mapa = Map.query.get(map_id)
        subscriptions= Subscription.query.filter_by(map=mapa)
        return SubscriptionSchema().dump(subscriptions, many=True).data, 200


class SubscriptionResource(Resource):
    def get(self, subscription_id):
        subscription = Subscription.query.get(subscription_id)
        return SubscriptionSchema().dump(subscription).data, 200

    def delete(self, subscription_id):
        subscription = Subscription.query.get(subscription_id)
        subscription.delete()
        return res, 204


class SubscriptionListResource(Resource):
    def get(self):
        res = []
        for subscription in Subscription.query.all():
            res.append(SubscriptionSchema().dump(subscription).data)
        return res

    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('map_id',  type=int)
        parser.add_argument('username')

        args = parser.parse_args()
        new_subscription = Subscription(args['map_id'], username=args['username'], subscription_time=datetime.now())
        new_subscription.save()
        
        return SubscriptionSchema().dump(new_subscription).data, 201