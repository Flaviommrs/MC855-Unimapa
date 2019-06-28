# -*- coding: utf-8 -*-
from dotenv import load_dotenv
import os

load_dotenv()

API_KEY = os.getenv("API_KEY", None)
FCM_KEY = os.getenv("FCM_KEY", None)
FIREBASE_PROJECT_ID = os.getenv("FCM_ID", None)
DATABASE_CONFIG = os.getenv("DATABASE_CONFIG", None)
DATABASE_CONFIG_LOCAL = os.getenv("DATABASE_CONFIG_LOCAL", None)
FIREBASE_UID = os.getenv("FIREBASE_UID", None)
FIREBASE_NAME = os.getenv("FIREBASE_NAME", None)
FIREBASE_EMAIL = os.getenv("FIREBASE_EMAIL", None)