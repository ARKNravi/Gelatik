from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from typing import List
from app.api.v1.endpoints.auth import get_db
from app.api.v1.endpoints.user import get_current_user
from app.models.user_model import User
from app.repositories.summary_repository import SummaryRepository
from app.api.v1.schemas.summary_schemas import (
    SummaryCreate,
    SummaryUpdate,
    SummaryPublish,
    SummaryResponse,
    CommentCreate,
    CommentResponse
)

router = APIRouter()

def get_summary_repository(db: Session = Depends(get_db)) -> SummaryRepository:
    return SummaryRepository(db)

@router.post("", response_model=SummaryResponse)
async def create_summary(
    summary: SummaryCreate,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Create a new summary with only content"""
    return summary_repository.create_summary(summary, current_user.id)

@router.get("", response_model=List[SummaryResponse])
async def get_summaries(
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Get all summaries that are either published or owned by the current user"""
    return summary_repository.get_summaries(current_user.id)

@router.get("/published", response_model=List[SummaryResponse])
async def get_published_summaries(
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Get all published summaries"""
    return summary_repository.get_summaries(current_user.id, published_only=True)

@router.get("/private", response_model=List[SummaryResponse])
async def get_private_summaries(
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Get only the current user's private summaries"""
    return summary_repository.get_summaries(current_user.id, private_only=True)

@router.get("/{summary_id}", response_model=SummaryResponse)
async def get_summary(
    summary_id: int,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Get a specific summary"""
    return summary_repository.get_summary(summary_id, current_user.id)

@router.put("/{summary_id}", response_model=SummaryResponse)
async def update_summary(
    summary_id: int,
    summary_update: SummaryUpdate,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Update a summary's content and image_url (only by the creator)"""
    return summary_repository.update_summary(summary_id, summary_update, current_user.id)

@router.post("/{summary_id}/publish", response_model=SummaryResponse)
async def publish_summary(
    summary_id: int,
    publish_data: SummaryPublish,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Publish a summary with title, subtitle, topic, and content (only by the creator)"""
    return summary_repository.publish_summary(summary_id, publish_data, current_user.id)

@router.delete("/{summary_id}")
async def delete_summary(
    summary_id: int,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Delete a summary (only by the creator)"""
    summary_repository.delete_summary(summary_id, current_user.id)
    return {"message": "Summary successfully deleted"}

@router.post("/{summary_id}/comments", response_model=CommentResponse)
async def create_comment(
    summary_id: int,
    comment: CommentCreate,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Create a comment on a published summary"""
    return summary_repository.create_comment(summary_id, comment, current_user.id)

@router.post("/{summary_id}/like")
async def toggle_like(
    summary_id: int,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Toggle like status for a published summary"""
    is_liked = summary_repository.toggle_like(summary_id, current_user.id)
    return {"message": "Summary liked" if is_liked else "Summary unliked"}

@router.post("/{summary_id}/bookmark")
async def toggle_bookmark(
    summary_id: int,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Toggle bookmark status for a published summary"""
    is_bookmarked = summary_repository.toggle_bookmark(summary_id, current_user.id)
    return {"message": "Summary bookmarked" if is_bookmarked else "Summary unbookmarked"} 