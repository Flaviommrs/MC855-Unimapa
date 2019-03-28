# -*- coding: utf-8 -*-
import unittest

from src.models import User, Map

class TestMap(unittest.TestCase):

    def test_cosntructor(self):
        owner = User('owner')
        map_ = Map('test map', owner)
        self.assertEqual(map_.name, 'test map') 
        self.assertEqual(map_.owner, owner)

if __name__ == '__main__':
    unittest.main()