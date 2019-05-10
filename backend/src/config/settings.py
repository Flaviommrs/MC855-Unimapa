# -*- coding: utf-8 -*-
from dotenv import load_dotenv
import os

load_dotenv()

FCM_KEY = os.getenv("FCM_KEY", None)
FCM_TEST_USER = os.getenv("FCM_TEST_USER", None)
FIREBASE_PROJECT_ID = os.getenv("FCM_ID", None)
DATABASE_CONFIG = os.getenv("DATABASE_CONFIG", None)