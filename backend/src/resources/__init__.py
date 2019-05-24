from .user_resource import api_bp as user_bp
from .map_resource import api_bp as map_bp
from .subscription_resource import api_bp as subscription_bp
from .post_resource import api_bp as post_bp

from .signup_resource import api_bp as signup_bp
from .account_resource import api_bp as account_bp
from .token_resource import api_bp as token_bp

__all__ = [
    'user_bp'
    'map_bp'
    'subscription_bp'
    'post_bp'
    
    'signup_bp'
    'account_bp'
    'token_bp'
]
