from datetime import datetime
from typing import Optional
from pydantic import BaseModel, conint


class FeedbackBase(BaseModel):
    rating: conint(ge=1, le=5)
    description: Optional[str] = None


class FeedbackCreate(FeedbackBase):
    pass


class FeedbackUpdate(FeedbackBase):
    pass


class Feedback(FeedbackBase):
    id: int
    user_id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        from_attributes = True


class FeedbackDosenBase(BaseModel):
    rating: conint(ge=1, le=5)
    description: Optional[str] = None


class FeedbackDosenCreate(FeedbackDosenBase):
    pass


class FeedbackDosenUpdate(FeedbackDosenBase):
    pass


class FeedbackDosen(FeedbackDosenBase):
    id: int
    user_id: int
    created_at: datetime
    updated_at: Optional[datetime] = None

    class Config:
        from_attributes = True


class PaginatedFeedbackResponse(BaseModel):
    items: list[Feedback]
    total: int


class PaginatedFeedbackDosenResponse(BaseModel):
    items: list[FeedbackDosen]
    total: int 