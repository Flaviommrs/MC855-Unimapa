import argparse
import json

from urllib import request
from flask_restful import Resource
from firebase_admin import auth

from ..config import settings

API_KEY = settings.API_KEY

class TokenResource(Resource):
    def get(self, uid):
        token = auth.create_custom_token(uid)
        data = {
            'token': token.decode("utf-8"),
            'returnSecureToken': True
        }

        url = "https://www.googleapis.com/identitytoolkit/v3/relyingparty" \
                "/verifyCustomToken?key={}".format(API_KEY)

        req = request.Request(url,
                                json.dumps(data).encode("utf-8"),
                                {'Content-Type': 'application/json'})
        response = request.urlopen(req).read()

        return json.loads(response)["idToken"]