from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.core.security import oauth2_scheme
from app.models.user_model import User
from app.api.v1.schemas.user_schemas import (
    UserProfile, UserProfileUpdate, VerifyPasswordRequest,
    VerifyPasswordResponse, ChangePasswordRequest, DeleteUserRequest,
    ErrorResponse, ErrorResponseWithHeaders
)
from typing import Optional
from app.api.v1.endpoints.auth import get_db
from jose import jwt, JWTError, ExpiredSignatureError
from app.core.config import settings
from app.repositories.user_repository import UserRepository
from app.services.user_service import UserService
from app.usecases.user_usecases import UserUseCases
from datetime import datetime, timedelta
from fastapi.responses import JSONResponse

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
    except ExpiredSignatureError:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Token has expired",
            headers={"WWW-Authenticate": "Bearer"},
        )
    except JWTError:
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

def get_error_response(status_code: int, error_code: str, message: str, headers: dict = None) -> JSONResponse:
    content = {
        "status_code": status_code,
        "error_code": error_code,
        "message": message
    }
    if headers:
        content["headers"] = headers
    return JSONResponse(status_code=status_code, content=content)

@router.get("/profile", response_model=UserProfile, responses={
    401: {"model": ErrorResponseWithHeaders, "description": "Authentication failed"},
    404: {"model": ErrorResponse, "description": "User not found"}
})
async def get_profile(
    current_user: User = Depends(get_current_user),
    user_usecases: UserUseCases = Depends(get_user_usecases)
):
    """
    Get the profile of the currently logged-in user.
    """
    try:
        return user_usecases.get_user_profile(current_user.id)
    except HTTPException as e:
        return get_error_response(
            e.status_code,
            "USER_NOT_FOUND",
            e.detail
        )

@router.put("/profile", response_model=UserProfile, responses={
    401: {"model": ErrorResponseWithHeaders, "description": "Authentication failed"},
    404: {"model": ErrorResponse, "description": "User not found"},
    422: {"model": ErrorResponse, "description": "Validation error"}
})
async def update_profile(
    profile_update: UserProfileUpdate,
    current_user: User = Depends(get_current_user),
    user_usecases: UserUseCases = Depends(get_user_usecases)
):
    """
    Update the profile of the currently logged-in user.
    """
    try:
        return user_usecases.update_user_profile(current_user.id, profile_update)
    except HTTPException as e:
        return get_error_response(
            e.status_code,
            "PROFILE_UPDATE_FAILED",
            e.detail
        )

@router.post("/verify-password", response_model=VerifyPasswordResponse, responses={
    401: {"model": ErrorResponseWithHeaders, "description": "Authentication failed"},
    400: {"model": ErrorResponse, "description": "Invalid password"}
})
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
        return get_error_response(
            status.HTTP_400_BAD_REQUEST,
            "INVALID_PASSWORD",
            "Invalid current password"
        )

@router.post("/change-password", responses={
    401: {"model": ErrorResponseWithHeaders, "description": "Authentication failed"},
    400: {"model": ErrorResponse, "description": "Password change error"},
    422: {"model": ErrorResponse, "description": "Validation error"}
})
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
        payload = jwt.decode(
            change_request.verification_token,
            settings.SECRET_KEY,
            algorithms=[settings.ALGORITHM]
        )
        
        if (payload.get("type") != "password_change" or 
            payload.get("sub") != current_user.email):
            return get_error_response(
                status.HTTP_400_BAD_REQUEST,
                "INVALID_TOKEN",
                "Invalid verification token"
            )
            
        user_repository.change_password(
            current_user, 
            change_request.new_password,
            change_request.verification_token
        )
        return {"message": "Password successfully changed"}
        
    except ExpiredSignatureError:
        return get_error_response(
            status.HTTP_400_BAD_REQUEST,
            "TOKEN_EXPIRED",
            "Verification token has expired. Please verify your current password again."
        )
    except JWTError:
        return get_error_response(
            status.HTTP_400_BAD_REQUEST,
            "INVALID_TOKEN",
            "Invalid verification token"
        )

@router.delete("/account", status_code=status.HTTP_200_OK, responses={
    401: {"model": ErrorResponseWithHeaders, "description": "Authentication failed"},
    400: {"model": ErrorResponse, "description": "Account deletion error"},
    422: {"model": ErrorResponse, "description": "Validation error"}
})
async def delete_account(
    delete_request: DeleteUserRequest,
    current_user: User = Depends(get_current_user),
    user_usecases: UserUseCases = Depends(get_user_usecases)
):
    """
    Delete user account. Requires current password and confirmation text.
    The confirmation text must be exactly "DELETE MY ACCOUNT".
    """
    try:
        user_usecases.delete_user(current_user.id, delete_request.password)
        return {"message": "Account successfully deleted"}
    except HTTPException as e:
        return get_error_response(
            e.status_code,
            "ACCOUNT_DELETION_FAILED",
            e.detail,
            {"reason": str(e)}
        ) 