from pydantic import BaseModel, conint, Field
from typing import Optional, List
from datetime import datetime

class FeedbackBase(BaseModel):
    rating: conint(ge=1, le=5) = Field(..., description="Rating from 1 to 5")
    description: Optional[str] = Field(None, min_length=1, max_length=1000)

class FeedbackCreate(FeedbackBase):
    pass

class FeedbackUpdate(FeedbackBase):
    pass

class Feedback(FeedbackBase):
    id: int
    user_id: int
    created_at: datetime
    updated_at: Optional[datetime]

    class Config:
        from_attributes = True

class FeedbackDosenCreate(FeedbackBase):
    pass

class FeedbackDosenUpdate(FeedbackBase):
    pass

class FeedbackDosen(FeedbackBase):
    id: int
    user_id: int
    created_at: datetime
    updated_at: Optional[datetime]

    class Config:
        from_attributes = True

class PaginatedFeedbackResponse(BaseModel):
    items: List[Feedback]
    total: int

class PaginatedFeedbackDosenResponse(BaseModel):
    items: List[FeedbackDosen]
    total: int
