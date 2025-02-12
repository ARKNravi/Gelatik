from pydantic import BaseModel, EmailStr, constr, validator, HttpUrl
from datetime import date
from typing import Literal, Optional, Dict, Any
import re
from app.models.user_model import IdentityType

class UserBase(BaseModel):
    email: EmailStr
    full_name: str
    birth_date: date
    identity_type: Literal['tuli', 'dengar', 'admin', 'jbi', 'dosen']

class UserCreate(UserBase):
    password: constr(min_length=8)
    password_confirm: str

    @validator('password')
    def validate_password(cls, v):
        if not re.search(r'[a-z]', v):
            raise ValueError('Password must contain at least one lowercase letter')
        if not re.search(r'[A-Z]', v):
            raise ValueError('Password must contain at least one uppercase letter')
        if not re.search(r'\d', v):
            raise ValueError('Password must contain at least one number')
        if not re.search(r'[!@#$%^&*(),.?":{}|<>]', v):
            raise ValueError('Password must contain at least one special character')
        return v

    @validator('password_confirm')
    def passwords_match(cls, v, values, **kwargs):
        if 'password' in values and v != values['password']:
            raise ValueError('Passwords do not match')
        return v

class UserLogin(BaseModel):
    email: EmailStr
    password: str

class Token(BaseModel):
    access_token: str
    token_type: str

class TokenData(BaseModel):
    email: str | None = None

class UserProfile(BaseModel):
    id: int
    email: EmailStr
    full_name: str
    birth_date: date
    identity_type: Literal['tuli', 'dengar', 'admin', 'jbi', 'dosen']
    institution: Optional[str] = None
    profile_picture_url: Optional[str] = None
    points: int = 0

    @validator('identity_type', pre=True)
    def convert_identity_type(cls, v):
        if isinstance(v, IdentityType):
            return v.value.lower()
        return v.lower() if v else v

    class Config:
        from_attributes = True

class UserProfileUpdate(BaseModel):
    full_name: Optional[str] = None
    birth_date: Optional[date] = None
    institution: Optional[str] = None
    profile_picture_url: Optional[str] = None

    class Config:
        from_attributes = True

class UserResponse(UserBase):
    id: int

    class Config:
        from_attributes = True

class VerifyPasswordRequest(BaseModel):
    current_password: str

class VerifyPasswordResponse(BaseModel):
    verification_token: str

class ChangePasswordRequest(BaseModel):
    verification_token: str
    new_password: constr(min_length=8)
    new_password_confirm: str

    @validator('new_password')
    def validate_password(cls, v):
        if not re.search(r'[a-z]', v):
            raise ValueError('Password must contain at least one lowercase letter')
        if not re.search(r'[A-Z]', v):
            raise ValueError('Password must contain at least one uppercase letter')
        if not re.search(r'\d', v):
            raise ValueError('Password must contain at least one number')
        if not re.search(r'[!@#$%^&*(),.?":{}|<>]', v):
            raise ValueError('Password must contain at least one special character')
        return v

    @validator('new_password_confirm')
    def passwords_match(cls, v, values, **kwargs):
        if 'new_password' in values and v != values['new_password']:
            raise ValueError('Passwords do not match')
        return v

class DeleteUserRequest(BaseModel):
    password: str
    confirm_text: str

    @validator('confirm_text')
    def validate_confirm_text(cls, v):
        if v != "DELETE MY ACCOUNT":
            raise ValueError('Please type "DELETE MY ACCOUNT" to confirm')
        return v

class ErrorResponse(BaseModel):
    status_code: int
    error_code: str
    message: str
    details: Optional[Dict[str, Any]] = None

class ErrorResponseWithHeaders(ErrorResponse):
    headers: Dict[str, str]
