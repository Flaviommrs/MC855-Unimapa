import os
import json
import tempfile

import pytest

from src.unimapa import create_app
from src.models import db as _db
from src.database import mock_database

from src.config import settings

TESTDB = 'test_project.db'
TESTDB_PATH = "/tmp/{}".format(TESTDB)
TEST_DATABASE_URI = 'sqlite:///' + TESTDB_PATH


@pytest.fixture(scope='session')
def app(request):
    os.environ['FLASK_ENV'] = 'development'
    
    app = create_app({
        'TESTING': True,
        'SQLALCHEMY_DATABASE_URI': TEST_DATABASE_URI,
    })

    ctx = app.app_context()
    ctx.push()

    def teardown():
        ctx.pop()

    request.addfinalizer(teardown)

    return app

@pytest.fixture(scope='session')
def db(app, request):
    """Session-wide test database."""
    if os.path.exists(TESTDB_PATH):
        os.unlink(TESTDB_PATH)

    def teardown():
        _db.drop_all()
        os.unlink(TESTDB_PATH)

    _db.app = app
    _db.create_all()
    mock_database(_db)

    request.addfinalizer(teardown)
    return _db


@pytest.fixture(scope='function')
def session(db, request):
    """Creates a new database session for a test."""
    connection = db.engine.connect()
    transaction = connection.begin()

    options = dict(bind=connection, binds={})
    session = db.create_scoped_session(options=options)

    db.session = session

    def teardown():
        transaction.rollback()
        connection.close()
        session.remove()

    request.addfinalizer(teardown)
    return session


@pytest.fixture(scope='function')
def client(app, session, request):
    """Session-wide test database."""
    return app.test_client()


@pytest.fixture(scope='function')
def auth_client(client, request):
    """"""
    res = client.get('/token?uid={}'.format(settings.FIREBASE_UID))
    token = json.loads(res.data.decode('utf-8'))
    client.environ_base['HTTP_AUTHORIZATION'] = 'Bearer {}'.format(token)
    return client