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
    SummaryResponse
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
    """Create a new summary with only isi"""
    return summary_repository.create_summary(summary, current_user.id)

@router.get("", response_model=List[SummaryResponse])
async def get_summaries(
    include_published: bool = True,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """
    Get all summaries that are either:
    - Created by the current user
    - Published by others (if include_published is True)
    """
    return summary_repository.get_user_summaries(current_user.id, include_published)

@router.get("/my", response_model=List[SummaryResponse])
async def get_my_summaries(
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Get only the current user's summaries"""
    return summary_repository.get_user_summaries(current_user.id, include_published=False)

@router.get("/{summary_id}", response_model=SummaryResponse)
async def get_summary(
    summary_id: int,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Get a specific summary if the user has access to it"""
    summary = summary_repository.get_summary(summary_id)
    if not summary:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Summary not found"
        )
    
    # Check if user has access to this summary
    if summary.user_id != current_user.id and not summary.is_published:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You don't have access to this summary"
        )
    
    return summary

@router.put("/{summary_id}", response_model=SummaryResponse)
async def update_summary(
    summary_id: int,
    summary_update: SummaryUpdate,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Update only the isi of a summary (only by the creator)"""
    summary = summary_repository.get_summary(summary_id)
    if not summary:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Summary not found"
        )
    
    # Only the creator can update the summary
    if summary.user_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You can only update your own summaries"
        )

    # Cannot update published summaries
    if summary.is_published:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Cannot update published summaries"
        )
    
    return summary_repository.update_summary(summary, summary_update)

@router.delete("/{summary_id}", status_code=status.HTTP_200_OK)
async def delete_summary(
    summary_id: int,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """Delete a summary (only by the creator)"""
    summary = summary_repository.get_summary(summary_id)
    if not summary:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Summary not found"
        )
    
    # Only the creator can delete the summary
    if summary.user_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You can only delete your own summaries"
        )
    
    summary_repository.delete_summary(summary)
    return {"message": "Summary successfully deleted"}

@router.post("/{summary_id}/publish", response_model=SummaryResponse)
async def publish_summary(
    summary_id: int,
    publish_data: SummaryPublish,
    current_user: User = Depends(get_current_user),
    summary_repository: SummaryRepository = Depends(get_summary_repository)
):
    """
    Publish a summary and set its title, subtitle, and topic.
    Only the creator can publish and this action cannot be undone.
    """
    summary = summary_repository.get_summary(summary_id)
    if not summary:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Summary not found"
        )
    
    # Only the creator can publish the summary
    if summary.user_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You can only publish your own summaries"
        )

    # Cannot publish already published summaries
    if summary.is_published:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Summary is already published"
        )
    
    return summary_repository.publish_summary(summary, publish_data) 