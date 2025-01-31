from typing import Optional
from fastapi import APIRouter, Depends, status, HTTPException
from sqlalchemy.orm import Session

from app.database import get_db
from app.core.security import oauth2_scheme, decode_token
from app.models.user_model import User
from app.api.v1.schemas.feedback_schemas import (
    FeedbackCreate,
    FeedbackUpdate,
    Feedback,
    FeedbackDosenCreate,
    FeedbackDosenUpdate,
    FeedbackDosen,
    PaginatedFeedbackResponse,
    PaginatedFeedbackDosenResponse
)
from app.services.feedback_service import FeedbackService
from app.repositories.feedback_repository import FeedbackRepository

router = APIRouter()

async def get_current_user(
    token: str = Depends(oauth2_scheme),
    db: Session = Depends(get_db)
) -> User:
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    
    try:
        payload = decode_token(token)
        if payload is None:
            raise credentials_exception
        
        email: str = payload.get("sub")
        role: str = payload.get("role", "user")  # Get role from token, default to "user"
        if email is None:
            raise credentials_exception
    except Exception:
        raise credentials_exception
    
    user = db.query(User).filter(User.email == email).first()
    
    if user is None:
        raise credentials_exception
    
    # Add is_admin property based on role from JWT
    user.is_admin = role == "admin"
        
    return user

@router.post("/system", response_model=Feedback, status_code=status.HTTP_201_CREATED)
def create_system_feedback(
    feedback_data: FeedbackCreate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Create a new system feedback.
    Only one feedback per user is allowed.
    """
    feedback_repository = FeedbackRepository(db)
    feedback_service = FeedbackService(feedback_repository)
    return feedback_service.create_feedback(current_user, feedback_data)

@router.post("/dosen", response_model=FeedbackDosen, status_code=status.HTTP_201_CREATED)
def create_dosen_feedback(
    feedback_data: FeedbackDosenCreate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Create a new dosen feedback.
    Only one feedback per user is allowed.
    """
    feedback_repository = FeedbackRepository(db)
    feedback_service = FeedbackService(feedback_repository)
    return feedback_service.create_feedback_dosen(current_user, feedback_data)

@router.put("/system/{feedback_id}", response_model=Feedback)
def update_system_feedback(
    feedback_id: int,
    feedback_data: FeedbackUpdate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Update a system feedback.
    Only the feedback owner can update it.
    """
    feedback_repository = FeedbackRepository(db)
    feedback_service = FeedbackService(feedback_repository)
    return feedback_service.update_feedback(feedback_id, current_user, feedback_data)

@router.put("/dosen/{feedback_id}", response_model=FeedbackDosen)
def update_dosen_feedback(
    feedback_id: int,
    feedback_data: FeedbackDosenUpdate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Update a dosen feedback.
    Only the feedback owner can update it.
    """
    feedback_repository = FeedbackRepository(db)
    feedback_service = FeedbackService(feedback_repository)
    return feedback_service.update_feedback_dosen(feedback_id, current_user, feedback_data)

@router.get("/system", response_model=PaginatedFeedbackResponse)
def get_all_system_feedback(
    skip: int = 0,
    limit: int = 10,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Get all system feedback.
    Only admins can access this endpoint.
    """
    feedback_repository = FeedbackRepository(db)
    feedback_service = FeedbackService(feedback_repository)
    feedbacks, total = feedback_service.get_all_feedback(current_user, skip, limit)
    return {"items": feedbacks, "total": total}

@router.get("/dosen", response_model=PaginatedFeedbackDosenResponse)
def get_all_dosen_feedback(
    skip: int = 0,
    limit: int = 10,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Get all dosen feedback.
    Only admins can access this endpoint.
    """
    feedback_repository = FeedbackRepository(db)
    feedback_service = FeedbackService(feedback_repository)
    feedbacks, total = feedback_service.get_all_feedback_dosen(current_user, skip, limit)
    return {"items": feedbacks, "total": total}

@router.delete("/system/{feedback_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_system_feedback(
    feedback_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Delete a system feedback.
    Only admins or the feedback owner can delete it.
    """
    feedback_repository = FeedbackRepository(db)
    feedback_service = FeedbackService(feedback_repository)
    feedback_service.delete_feedback(feedback_id, current_user)

@router.delete("/dosen/{feedback_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_dosen_feedback(
    feedback_id: int,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db)
):
    """
    Delete a dosen feedback.
    Only admins or the feedback owner can delete it.
    """
    feedback_repository = FeedbackRepository(db)
    feedback_service = FeedbackService(feedback_repository)
    feedback_service.delete_feedback_dosen(feedback_id, current_user)
