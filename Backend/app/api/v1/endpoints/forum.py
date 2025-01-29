from fastapi import APIRouter, Depends, HTTPException, status, Query
from sqlalchemy.orm import Session
from typing import List, Optional
from app.api.v1.endpoints.auth import get_db
from app.api.v1.endpoints.user import get_current_user
from app.models.user_model import User
from app.repositories.forum_repository import ForumRepository
from app.api.v1.schemas.forum_schemas import (
    ForumCreate,
    ForumUpdate,
    ForumResponse,
    CommentCreate,
    CommentResponse,
    ForumFilter
)

router = APIRouter()

def get_forum_repository(db: Session = Depends(get_db)) -> ForumRepository:
    return ForumRepository(db)

@router.post("", response_model=ForumResponse)
async def create_forum(
    forum: ForumCreate,
    current_user: User = Depends(get_current_user),
    forum_repository: ForumRepository = Depends(get_forum_repository)
):
    """Create a new forum post"""
    return forum_repository.create_forum(forum, current_user.id)

@router.get("", response_model=List[ForumResponse])
async def get_forums(
    topik: Optional[str] = None,
    search: Optional[str] = None,
    current_user: User = Depends(get_current_user),
    forum_repository: ForumRepository = Depends(get_forum_repository)
):
    """Get all forums with optional filtering by topic and search"""
    filter = ForumFilter(topik=topik, search=search)
    return forum_repository.get_forums(current_user.id, filter)

@router.get("/{forum_id}", response_model=ForumResponse)
async def get_forum(
    forum_id: int,
    current_user: User = Depends(get_current_user),
    forum_repository: ForumRepository = Depends(get_forum_repository)
):
    """Get a specific forum by ID"""
    return forum_repository.get_forum(forum_id, current_user.id)

@router.put("/{forum_id}", response_model=ForumResponse)
async def update_forum(
    forum_id: int,
    forum_update: ForumUpdate,
    current_user: User = Depends(get_current_user),
    forum_repository: ForumRepository = Depends(get_forum_repository)
):
    """Update a forum (only by the creator)"""
    return forum_repository.update_forum(forum_id, forum_update, current_user.id)

@router.delete("/{forum_id}")
async def delete_forum(
    forum_id: int,
    current_user: User = Depends(get_current_user),
    forum_repository: ForumRepository = Depends(get_forum_repository)
):
    """Delete a forum (only by the creator)"""
    forum_repository.delete_forum(forum_id, current_user.id)
    return {"message": "Forum successfully deleted"}

@router.post("/{forum_id}/comments", response_model=CommentResponse)
async def create_comment(
    forum_id: int,
    comment: CommentCreate,
    current_user: User = Depends(get_current_user),
    forum_repository: ForumRepository = Depends(get_forum_repository)
):
    """Create a comment on a forum"""
    return forum_repository.create_comment(forum_id, comment, current_user.id)

@router.post("/{forum_id}/like")
async def toggle_like(
    forum_id: int,
    current_user: User = Depends(get_current_user),
    forum_repository: ForumRepository = Depends(get_forum_repository)
):
    """Toggle like status for a forum"""
    is_liked = forum_repository.toggle_like(forum_id, current_user.id)
    return {"message": "Forum liked" if is_liked else "Forum unliked"}
