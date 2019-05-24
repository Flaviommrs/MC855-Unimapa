import json
import urllib

from firebase_admin import auth
from flask import request, Blueprint
from flask_restful import Resource, reqparse, Api

from ..config import settings
from ..models import User

api_bp = Blueprint('token_api', __name__)
api = Api(api_bp)

API_KEY = settings.API_KEY

class TokenResource(Resource):
    def get(self):
        uid = request.args.get('uid', None)

        if not User.exists(User, uid):
            return 'User does not exist', 400

        token = auth.create_custom_token(uid)
        data = {
            'token': token.decode("utf-8"),
            'returnSecureToken': True
        }

        url = "https://www.googleapis.com/identitytoolkit/v3/relyingparty" \
                "/verifyCustomToken?key={}".format(API_KEY)

        req = urllib.request.Request(url, json.dumps(data).encode("utf-8"), {'Content-Type': 'application/json'})
        response = urllib.request.urlopen(req).read()

        return json.loads(response)["idToken"]