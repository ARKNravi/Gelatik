from pydantic import BaseModel, EmailStr, Field, validator
from datetime import date
from typing import Optional, Dict, Any
from app.models.user_model import IdentityType

class UserBase(BaseModel):
    email: EmailStr
    full_name: str = Field(..., min_length=1, max_length=100)
    birth_date: date
    identity_type: IdentityType

    @validator('identity_type', pre=True)
    def uppercase_identity_type(cls, v):
        if isinstance(v, str):
            return v.upper()
        return v

class UserCreate(UserBase):
    password: str = Field(..., min_length=8)
    password_confirm: str

    @validator('password_confirm')
    def passwords_match(cls, v, values, **kwargs):
        if 'password' in values and v != values['password']:
            raise ValueError('Passwords do not match')
        return v

class UserLogin(BaseModel):
    email: EmailStr
    password: str

class UserUpdate(BaseModel):
    full_name: Optional[str] = Field(None, min_length=1, max_length=100)
    institution: Optional[str] = Field(None, max_length=100)
    profile_picture_url: Optional[str] = Field(None, max_length=500)

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    email: Optional[str] = None

class ErrorResponse(BaseModel):
    status_code: int
    error_code: str
    message: str
    details: Optional[Dict[str, Any]] = None

class ErrorResponseWithHeaders(ErrorResponse):
    headers: Dict[str, str] 