from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.security import oauth2_scheme
from app.models.user_model import User
from app.api.v1.schemas.user_schemas import (
    UserProfile, UserProfileUpdate, VerifyPasswordRequest,
    VerifyPasswordResponse, ChangePasswordRequest
)
from typing import Optional
from app.api.v1.endpoints.auth import get_db
import jwt
from app.core.config import settings
from app.repositories.user_repository import UserRepository
from app.services.user_service import UserService
from app.usecases.user_usecases import UserUseCases
from datetime import datetime, timedelta

router = APIRouter()

async def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)) -> User:
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, settings.SECRET_KEY, algorithms=[settings.ALGORITHM])
        email: str = payload.get("sub")
        if email is None:
            raise credentials_exception
    except jwt.JWTError:
        raise credentials_exception

    user_repository = UserRepository(db)
    user = user_repository.get_user_by_email(email)
    if user is None:
        raise credentials_exception
    return user

def get_user_usecases(db: Session = Depends(get_db)) -> UserUseCases:
    user_repository = UserRepository(db)
    user_service = UserService(user_repository)
    return UserUseCases(user_service)

@router.get("/profile", response_model=UserProfile)
async def get_profile(
    current_user: User = Depends(get_current_user),
    user_usecases: UserUseCases = Depends(get_user_usecases)
):
    """
    Get the profile of the currently logged-in user.
    """
    return user_usecases.get_user_profile(current_user.id)

@router.put("/profile", response_model=UserProfile)
async def update_profile(
    profile_update: UserProfileUpdate,
    current_user: User = Depends(get_current_user),
    user_usecases: UserUseCases = Depends(get_user_usecases)
):
    """
    Update the profile of the currently logged-in user.
    """
    return user_usecases.update_user_profile(current_user.id, profile_update)

@router.post("/verify-password", response_model=VerifyPasswordResponse)
async def verify_current_password(
    verify_request: VerifyPasswordRequest,
    current_user: User = Depends(get_current_user),
    user_repository: UserRepository = Depends(lambda db=Depends(get_db): UserRepository(db))
):
    """
    Verify the current password before allowing password change.
    Returns a verification token valid for 5 minutes.
    """
    try:
        user_repository.verify_password(current_user, verify_request.current_password)
        
        # Generate a special token valid for 5 minutes
        verification_token = jwt.encode(
            {
                "sub": current_user.email,
                "type": "password_change",
                "exp": datetime.utcnow() + timedelta(minutes=5)
            },
            settings.SECRET_KEY,
            algorithm=settings.ALGORITHM
        )
        
        return {"verification_token": verification_token}
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid current password"
        )

@router.post("/change-password")
async def change_password(
    change_request: ChangePasswordRequest,
    current_user: User = Depends(get_current_user),
    user_repository: UserRepository = Depends(lambda db=Depends(get_db): UserRepository(db))
):
    """
    Change the user's password after verifying the token from the previous step.
    The verification token can only be used once.
    """
    try:
        # Verify the special password change token
        payload = jwt.decode(
            change_request.verification_token,
            settings.SECRET_KEY,
            algorithms=[settings.ALGORITHM]
        )
        
        if (payload.get("type") != "password_change" or 
            payload.get("sub") != current_user.email):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Invalid verification token"
            )
            
        user_repository.change_password(
            current_user, 
            change_request.new_password,
            change_request.verification_token
        )
        return {"message": "Password successfully changed"}
        
    except jwt.ExpiredSignatureError:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Verification token has expired. Please verify your current password again."
        )
    except jwt.JWTError:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invalid verification token"
        )
