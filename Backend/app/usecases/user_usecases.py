from app.services.user_service import UserService
from app.api.v1.schemas.user_schemas import UserProfileUpdate, UserProfile
from fastapi import HTTPException

class UserUseCases:
    def __init__(self, user_service: UserService):
        self.user_service = user_service

    def get_user_profile(self, user_id: int) -> UserProfile:
        """
        Get user profile information
        """
        return self.user_service.get_user_profile(user_id)

    def update_user_profile(self, user_id: int, profile_update: UserProfileUpdate) -> UserProfile:
        """
        Update user profile information
        """
        return self.user_service.update_user_profile(user_id, profile_update)
