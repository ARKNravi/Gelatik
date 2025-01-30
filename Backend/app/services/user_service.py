from app.repositories.user_repository import UserRepository
from app.api.v1.schemas.user_schemas import UserProfileUpdate, UserProfile
from fastapi import HTTPException, status
from app.core.exceptions import InvalidPasswordError
from app.models.user_model import IdentityType

class UserService:
    def __init__(self, user_repository: UserRepository):
        self.user_repository = user_repository

    def get_user_profile(self, user_id: int) -> UserProfile:
        user = self.user_repository.get_user_by_id(user_id)
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )
        # Convert enum to string for response
        user_dict = {
            "id": user.id,
            "email": user.email,
            "full_name": user.full_name,
            "birth_date": user.birth_date,
            "identity_type": user.identity_type.value if user.identity_type else None,
            "institution": user.institution,
            "profile_picture_url": user.profile_picture_url,
            "points": user.points
        }
        return UserProfile(**user_dict)

    def update_user_profile(self, user_id: int, profile_update: UserProfileUpdate) -> UserProfile:
        user = self.user_repository.get_user_by_id(user_id)
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )
        try:
            updated_user = self.user_repository.update_profile(user, profile_update)
            # Convert enum to string for response
            user_dict = {
                "id": updated_user.id,
                "email": updated_user.email,
                "full_name": updated_user.full_name,
                "birth_date": updated_user.birth_date,
                "identity_type": updated_user.identity_type.value if updated_user.identity_type else None,
                "institution": updated_user.institution,
                "profile_picture_url": updated_user.profile_picture_url,
                "points": updated_user.points
            }
            return UserProfile(**user_dict)
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
