from pydantic import BaseModel, Field, HttpUrl
from typing import Optional, List
from datetime import date, datetime
from app.api.v1.schemas.user_schemas import UserProfile

class TranslationBase(BaseModel):
    name: str = Field(..., min_length=1, max_length=100)
    alamat: str = Field(..., min_length=1, max_length=255)
    profile_pic: Optional[str] = Field(None, max_length=500)

class TranslationCreate(TranslationBase):
    pass

class TranslationUpdate(BaseModel):
    name: Optional[str] = Field(None, min_length=1, max_length=100)
    alamat: Optional[str] = Field(None, min_length=1, max_length=255)
    availability: Optional[bool] = None
    profile_pic: Optional[str] = Field(None, max_length=500)

class TranslationResponse(TranslationBase):
    id: int
    availability: bool
    user_id: int

    class Config:
        from_attributes = True

class TranslatorBase(BaseModel):
    name: str = Field(..., min_length=1, max_length=100)
    alamat: str = Field(..., min_length=1, max_length=255)
    availability: bool = True
    profile_pic: Optional[HttpUrl] = None

class TranslatorCreate(TranslatorBase):
    pass

class TranslatorUpdate(TranslatorBase):
    pass

class Translation(TranslatorBase):
    id: int
    user_id: int

    class Config:
        from_attributes = True

class TranslationOrderBase(BaseModel):
    tanggal: date
    time_slot: str = Field(..., description="Time slot in format '08.00 - 09.00'")
    description: str = Field(..., min_length=1, max_length=500)
    translator_id: int

class TranslationOrderCreate(TranslationOrderBase):
    pass

class TranslationOrderUpdate(BaseModel):
    status: str = Field(..., pattern="^(pending|confirmed|cancelled|completed)$")

class TranslationOrder(TranslationOrderBase):
    id: int
    status: str
    created_at: datetime
    updated_at: Optional[datetime]
    user_id: int
    translator: Optional[Translation] = None
    user: Optional[UserProfile] = None

    @property
    def user_identity_type(self) -> str:
        if self.user:
            return self.user.identity_type.lower()
        return None

    class Config:
        from_attributes = True

class PaginatedTranslatorResponse(BaseModel):
    items: List[Translation]
    total: int

class PaginatedOrderResponse(BaseModel):
    items: List[TranslationOrder]
    total: int
