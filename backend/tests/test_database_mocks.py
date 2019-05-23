import os
import tempfile

import pytest

from src.database import mock_database_users, mock_database_maps, mock_database_posts, mock_database_subscriptions
from src.models import db, User, Map, Post, Subscription

def test_database_mock_not_test_users(session, request):
    """Test create_app without passing test config."""
    NEW_USERS = 5
    os.environ['FLASK_ENV'] = 'not_development'

    users = User.query.all()
    assert len(users) == 0
    
    mock_database_users(session, NEW_USERS)
    users = User.query.all()
    assert len(users) == 0

    def teardown():
        os.environ['FLASK_ENV'] = 'development'

    request.addfinalizer(teardown)

def test_database_mock_users(session):
    """Test create_app without passing test config."""
    NEW_USERS = 5
    users = User.query.all()
    assert len(users) == 0
    
    mock_database_users(session, NEW_USERS)
    users = User.query.all()
    assert len(users) == NEW_USERS


def test_database_mock_maps(session):
    """Test create_app without passing test config."""
    NEW_MAPS = 5
    maps = Map.query.all()
    assert len(maps) == 0
    
    mock_database_maps(session, NEW_MAPS)
    maps = Map.query.all()
    assert len(maps) == NEW_MAPS


def test_database_mock_subscriptions(session):
    """Test create_app without passing test config."""
    NEW_SUBSCRIPTIONS = 3
    subscritpions = Subscription.query.all()
    assert len(subscritpions) == 0

    mock_database_users(session, 10)
    mock_database_maps(session, 10)

    mock_database_subscriptions(session, NEW_SUBSCRIPTIONS)
    subscritpions = Subscription.query.all()
    assert len(subscritpions) == NEW_SUBSCRIPTIONS


def test_database_mock_posts(session):
    """Test create_app without passing test config."""
    NEW_POSTS = 10
    posts = Subscription.query.all()
    assert len(posts) == 0

    mock_database_users(session, 10)
    mock_database_maps(session, 10)

    mock_database_posts(session, NEW_POSTS)
    posts = Post.query.all()
    assert len(posts) == NEW_POSTS