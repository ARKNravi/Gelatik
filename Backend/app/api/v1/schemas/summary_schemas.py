from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

class SummaryBase(BaseModel):
    content: str = Field(..., min_length=1)

class SummaryCreate(SummaryBase):
    pass

class SummaryUpdate(BaseModel):
    content: Optional[str] = Field(None, min_length=1)
    image_url: Optional[str] = Field(None, max_length=500)

class SummaryPublish(BaseModel):
    title: str = Field(..., min_length=1, max_length=255)
    subtitle: Optional[str] = Field(None, max_length=255)
    topic: str = Field(..., min_length=1, max_length=100)
    content: str = Field(..., min_length=1)
    image_url: Optional[str] = Field(None, max_length=500)

class CommentCreate(BaseModel):
    content: str = Field(..., min_length=1)

class CommentResponse(BaseModel):
    id: int
    content: str
    created_at: datetime
    updated_at: Optional[datetime]
    user_id: int
    summary_id: int
    user_name: str

    class Config:
        from_attributes = True

class SummaryResponse(BaseModel):
    id: int
    content: str
    title: Optional[str]
    subtitle: Optional[str]
    topic: Optional[str]
    image_url: Optional[str]
    is_published: bool
    created_at: datetime
    updated_at: Optional[datetime]
    user_id: int
    user_name: str
    like_count: int = 0
    comment_count: int = 0
    bookmark_count: int = 0
    has_liked: bool = False
    has_bookmarked: bool = False
    comments: List[CommentResponse] = []

    class Config:
        from_attributes = True 