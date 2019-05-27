# -*- coding: utf-8 -*-
from flask_sqlalchemy import SQLAlchemy
from flask import abort

import geojson
import os

db = SQLAlchemy()

class myModel():
    def get_or_create(self, id, **kwargs):
        instance = self.query.filter_by(id = id).first()
        if instance:
            return instance
        else:
            instance = self(id=id, **kwargs)
            db.session.add(instance)
            db.session.commit()
            return instance


    def exists(self, id):
        instance = self.query.filter_by(id = id).first()
        if instance:
            return True
        return False

class User(db.Model, myModel):
    id = db.Column(db.String, primary_key=True)
    email = db.Column(db.String(100), unique=True, nullable=False)
    name = db.Column(db.String(100))

    posts = db.relationship('Post', backref='user', lazy=True)
    subscriptions = db.relationship('Subscription', backref='user', lazy=True)
    notification_tokens = db.relationship('NotificationToken', backref='user', lazy=True)

    def __repr__(self):
        return '<User {}>'.format(self.email)

    
class Map(db.Model, myModel):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    
    posts = db.relationship('Post', backref='map', lazy=True)
    subscriptions = db.relationship('Subscription', backref='map', lazy=True)
    
class Post(db.Model, myModel):
    id = db.Column(db.Integer, primary_key=True)
    post_time = db.Column(db.DateTime, nullable=False)
    message = db.Column(db.Text)
    point_x = db.Column(db.Float)
    point_y = db.Column(db.Float)

    user_id = db.Column(db.String, db.ForeignKey('user.id'), nullable=False)
    map_id = db.Column(db.Integer, db.ForeignKey('map.id'), nullable=False)


class Subscription(db.Model, myModel):
    id = db.Column(db.Integer, primary_key=True)
    subscription_time = db.Column(db.DateTime, nullable=False)

    user_id = db.Column(db.String, db.ForeignKey('user.id'), nullable=False)
    map_id = db.Column(db.Integer, db.ForeignKey('map.id'), nullable=False)


class NotificationToken(db.Model, myModel):
    notification_token = db.Column(db.String, primary_key=True)

    user_id = db.Column(db.String, db.ForeignKey('user.id'), nullable=False)