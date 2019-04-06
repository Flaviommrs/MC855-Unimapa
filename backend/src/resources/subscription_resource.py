from flask_restful import Resource, reqparse


from ..schemas import SubscriptionSchema
from ..models import Subscription

from datetime import datetime

class UserSubscriptionResource(Resource):
    def get(self, username):
        subscriptions = Subscription.scan(username__eq=username)
        return SubscriptionSchema().dump(subscriptions, many=True).data, 200

class SubscriptionResource(Resource):
    def get(self, map_id):
        subscriptions = Subscription.query(map_id)
        return SubscriptionSchema().dump(subscriptions, many=True).data, 200

class SubscriptionListResource(Resource):
    def get(self):
        res = []
        for subscription in Subscription.scan():
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