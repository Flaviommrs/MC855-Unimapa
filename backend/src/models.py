# -*- coding: utf-8 -*-
from pynamodb.models import Model
from pynamodb.attributes import NumberAttribute, UnicodeAttribute, UTCDateTimeAttribute, JSONAttribute
import geojson
import os

class User(Model):   

    class Meta:
        table_name=  'unimapa_user'

    username = UnicodeAttribute(hash_key=True)
    name = UnicodeAttribute()

    
class Map(Model):

    class Meta:
        table_name = 'unimapa_map'

    map_id = NumberAttribute(hash_key=True)
    name = UnicodeAttribute()
    posts = NumberAttribute(default=0)

    
class Subscription(Model):

    class Meta:
        table_name = 'unimapa_subscription'

    map_id = NumberAttribute(hash_key=True)
    username = UnicodeAttribute(range_key=True)
    subscription_time = UTCDateTimeAttribute()

class Post(Model):

    class Meta:
        table_name = 'unimapa_post'

    map_id = NumberAttribute(hash_key=True)
    post_id = NumberAttribute(range_key=True)
    post_time = UTCDateTimeAttribute()
    username = UnicodeAttribute()
    message = UnicodeAttribute()
    point = JSONAttribute()


if 'FLASK_ENV' in os.environ and os.environ['FLASK_ENV'] == 'development':
    import sys, inspect
    for _, my_model in inspect.getmembers(sys.modules[__name__], lambda member: inspect.isclass(member) and member.__module__ == __name__ ):
        try:
            my_model.Meta.host = 'http://localhost:8000'
            if not my_model.exists():
                my_model.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)
        except:
            pass