import os
import json 
import pytest

from src.config import settings

from src.models import Post
from src.database import mock_database_posts, mock_database_users, mock_database_maps

def test_fail_token(client, request):
    """Test create_app without passing test config."""
    res = client.get('/token?uid=1')

    assert res.status_code == 400
    assert 'User does not exist' in res.data.decode('utf-8')


def test_get_token(client, request):
    """Test create_app without passing test config."""
    res = client.get('/token?uid={}'.format(settings.FIREBASE_UID))

    assert res.status_code == 200
    assert len(res.data) > 0