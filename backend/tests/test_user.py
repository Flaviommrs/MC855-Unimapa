# -*- coding: utf-8 -*-
import unittest

from src.models import User

from src.schemas import UserSchema

class TestUser(unittest.TestCase):

    def test_constructor(self):
        user = User('test name')
        self.assertEqual(user.name, 'test name')

    def test_user_schema_serialization(self):
        user = User('test name')

        schema = UserSchema()
        result = schema.dump(user)
        self.assertEqual(result.data['name'], 'test name')

    def test_user_schema_deserialization(self):
        user_data = {
            'name' : 'test name'
        }
        schema = UserSchema()
        user = schema.load(user_data)
        self.assertEqual(user.data.name, 'test name')

    def test_user_schema_serialization_deserialization(self):
        user = User('test name')

        schema = UserSchema()
        result = schema.dump(user)
        user2 = schema.load(result.data)
        self.assertEqual(user2.data.name, user.name)


if __name__ == '__main__':
    unittest.main()