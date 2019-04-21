from flask_restful import Resource, reqparse

from ..schemas import PostSchema
from ..models import Post

from datetime import datetime

class UserPostListResource(Resource):
    def get(self, username):
        posts = Post.scan(username__eq=username)
        return PostSchema().dump(posts, many=True).data, 200

class PostResource(Resource):
    def get(self, map_id):
        user = Post.query(map_id)
        return PostSchema().dump(user, many=True).data, 200

class PostListResource(Resource):
    def get(self):
        res = []
        for post in Post.scan():
            res.append(PostSchema().dump(post).data)
        return res, 200

    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument('map_id', type=int)
        parser.add_argument('post_id', type=int)
        parser.add_argument('username')
        parser.add_argument('message')
        parser.add_argument('pos_x', type=float)
        parser.add_argument('pos_y', type=float)

        args = parser.parse_args()
        new_post = Post(args['map_id'],
            args['post_id'],
            post_time=datetime.utcnow(),
            username=args['username'],
            message=args['message'],
            pos_x=args['pos_x'],
            pos_y=args['pos_y']
            )
        new_post.save()
        
        return PostSchema().dump(new_post).data, 201