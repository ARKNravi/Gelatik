from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

class ForumBase(BaseModel):
    judul: str = Field(..., min_length=1, max_length=255)
    subjudul: Optional[str] = Field(None, max_length=255)
    topik: str = Field(..., min_length=1, max_length=100)
    isi: str = Field(..., min_length=1)
    gambar_url: Optional[str] = Field(None, max_length=500)

class ForumCreate(ForumBase):
    pass

class ForumUpdate(ForumBase):
    pass

class CommentBase(BaseModel):
    content: str = Field(..., min_length=1)

class CommentCreate(CommentBase):
    pass

class CommentUpdate(CommentBase):
    pass

class CommentResponse(CommentBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime]
    user_id: int
    forum_id: int
    user_name: str

    class Config:
        from_attributes = True

class ForumResponse(ForumBase):
    id: int
    created_at: datetime
    updated_at: Optional[datetime]
    user_id: int
    user_name: str
    like_count: int
    comment_count: int
    has_liked: bool
    comments: List[CommentResponse] = []

    class Config:
        from_attributes = True

class ForumFilter(BaseModel):
    topik: Optional[str] = None
    search: Optional[str] = None
