from sqlalchemy.orm import Session
from app.models.user_model import User, IdentityType
from app.models.token_model import UsedToken
from app.api.v1.schemas.user_schemas import UserProfileUpdate
from app.core.security import verify_password, get_password_hash
from app.core.exceptions import InvalidPasswordError
from fastapi import HTTPException, status

class UserRepository:
    def __init__(self, db: Session):
        self.db = db

    def get_user_by_email(self, email: str) -> User | None:
        return self.db.query(User).filter(User.email == email).first()

    def get_user_by_id(self, user_id: int) -> User | None:
        return self.db.query(User).filter(User.id == user_id).first()

    def update_profile(self, user: User, profile_update: UserProfileUpdate) -> User:
        # Get only the fields that were actually provided in the update request
        update_data = profile_update.model_dump(exclude_unset=True)
        
        # Update only the fields that were provided
        for key, value in update_data.items():
            if value is not None:  # Only update if value is not None
                # Convert string identity_type to enum if needed
                if key == 'identity_type' and isinstance(value, str):
                    value = IdentityType(value)
                setattr(user, key, value)
        
        self.db.commit()
        self.db.refresh(user)
        return user

    def verify_password(self, user: User, current_password: str) -> bool:
        if not verify_password(current_password, user.password):
            raise InvalidPasswordError()
        return True

    def is_token_used(self, token: str) -> bool:
        """Check if a token has been used before"""
        return self.db.query(UsedToken).filter(UsedToken.token == token).first() is not None

    def invalidate_token(self, token: str):
        """Mark a token as used"""
        used_token = UsedToken(token=token)
        self.db.add(used_token)
        self.db.commit()

    def change_password(self, user: User, new_password: str, verification_token: str) -> User:
        # Check if token has been used
        if self.is_token_used(verification_token):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="This verification token has already been used"
            )

        # Mark the token as used
        self.invalidate_token(verification_token)

        # Change the password
        user.password = get_password_hash(new_password)
        self.db.commit()
        self.db.refresh(user)
        return user

    def delete_user(self, user: User, password: str) -> bool:
        """Delete a user after verifying their password"""
        if not verify_password(password, user.password):
            raise InvalidPasswordError()
            
        self.db.delete(user)
        self.db.commit()
        return True
