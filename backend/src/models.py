# -*- coding: utf-8 -*-

class User(object):    
    def __init__(self, name):
        self.name = name

    
class Map(object):
    def __init__(self, name, owner):
        self.name = name
        self.owner = owner
    
    
class Position(object):
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def get_cordinates(self):
        return (self.x, self.y)


class Post(object):    
    def __init__(self, message, owner, x, y, map):
        self.message = message
        self.owner = owner
        self.position = Position(x, y)