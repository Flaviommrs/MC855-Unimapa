# -*- coding: utf-8 -*-
import unittest

from src.models import User

from src.schemas import UserSchema

class TestUser(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        super(TestUser, cls).setUpClass()
        User.Meta.host = "http://localhost:8000"
        if not User.exists():
            User.create_table(read_capacity_units=1, write_capacity_units=1, wait=True)

    def test_constructor(self):
        username = 'sample@email.com'
        name = 'sample name'
        user = User(username, name=name)
        
        self.assertEqual(user.name, name)
        self.assertEqual(user.username, username)

    def test_user_schema_serialization(self):
        username = 'sample@email.com'
        name = 'sample name'
        user = User(username, name=name)

        schema = UserSchema()
        result = schema.dump(user)
        self.assertEqual(result.data['username'], username)
        self.assertEqual(result.data['name'], name)

    def test_user_schema_deserialization(self):
        username = 'sample@email.com'
        name = 'sample name'
        user_data = {
            'username' : username,
            'name' : name
        }

        schema = UserSchema()
        user = schema.load(user_data)
        self.assertEqual(user.data.name, name)
        self.assertEqual(user.data.username, username)

    def test_user_schema_serialization_deserialization(self):
        username = 'sample@email.com'
        name = 'sample name'
        user = User(username, name=name)

        schema = UserSchema()
        result = schema.dump(user)
        user2 = schema.load(result.data)
        self.assertEqual(user2.data.name, user.name)


if __name__ == '__main__':
    unittest.main()