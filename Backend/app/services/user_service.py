from app.repositories.user_repository import UserRepository
from app.api.v1.schemas.user_schemas import UserProfileUpdate
from fastapi import HTTPException, status
from app.core.exceptions import InvalidPasswordError

class UserService:
    def __init__(self, user_repository: UserRepository):
        self.user_repository = user_repository

    def get_user_profile(self, user_id: int):
        user = self.user_repository.get_user_by_id(user_id)
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )
        return user

    def update_user_profile(self, user_id: int, profile_update: UserProfileUpdate):
        user = self.user_repository.get_user_by_id(user_id)
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )
        try:
            updated_user = self.user_repository.update_profile(user, profile_update)
            return updated_user
        except Exception as e:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update profile"
            )

    def delete_user(self, user_id: int, password: str) -> bool:
        """Delete a user account after verifying their password"""
        user = self.user_repository.get_user_by_id(user_id)
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )
        try:
            return self.user_repository.delete_user(user, password)
        except InvalidPasswordError:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid password"
            )
        except Exception as e:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to delete user account"
            )
