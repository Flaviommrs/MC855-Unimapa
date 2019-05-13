from flask import request
from flask_restful import Resource, reqparse

from ..schemas import MapSchema
from ..models import Map

from ..config import settings

class SignUpResource(Resource):
    def get(self):
        return "", 201

    def post(self):
        return "", 201