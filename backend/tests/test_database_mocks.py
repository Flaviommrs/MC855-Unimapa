import os
import tempfile

import pytest

from src.database import mock_database_users, mock_database_maps, mock_database_posts, mock_database_subscriptions
from src.models import db, User, Map, Post, Subscription

def test_database_mock_not_test_users(session, request):
    """Test create_app without passing test config."""
    def teardown():
        os.environ['FLASK_ENV'] = 'development'

    request.addfinalizer(teardown)

    NEW_USERS = 5
    os.environ['FLASK_ENV'] = 'not_development'

    count = User.query.count()
    mock_database_users(session, NEW_USERS)
    assert User.query.count() == count

    
def test_database_mock_users(session):
    """Test create_app without passing test config."""
    NEW_USERS = 5
    count = User.query.count()
    mock_database_users(session, NEW_USERS)
    assert User.query.count() == count + NEW_USERS


def test_database_mock_maps(session):
    """Test create_app without passing test config."""
    NEW_MAPS = 5

    count = Map.query.count()
    mock_database_maps(session, NEW_MAPS)
    assert len(Map.query.all()) == count + NEW_MAPS


def test_database_mock_subscriptions(session):
    """Test create_app without passing test config."""
    mock_database_users(session, 10)
    mock_database_maps(session, 10)

    NEW_SUBSCRIPTIONS = 3
    count = Subscription.query.count()

    mock_database_subscriptions(session, NEW_SUBSCRIPTIONS)
    assert Subscription.query.count() == count + NEW_SUBSCRIPTIONS


def test_database_mock_posts(session):
    """Test create_app without passing test config."""
    mock_database_users(session, 10)
    mock_database_maps(session, 10)

    NEW_POSTS = 10
    count = Post.query.count()

    mock_database_posts(session, NEW_POSTS)
    assert Post.query.count() == count + NEW_POSTS