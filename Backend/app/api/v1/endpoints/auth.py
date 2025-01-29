from fastapi import APIRouter, HTTPException, Depends, Body, Response
from sqlalchemy.orm import Session
from datetime import timedelta
from app.core.security import create_access_token, get_password_hash, verify_password, oauth2_scheme
from app.core.config import settings
from app.api.v1.schemas.user_schemas import (
    UserCreate, UserResponse, UserLogin, Token,
    ErrorResponse, ErrorResponseWithHeaders
)
from app.models.user_model import User
from sqlalchemy.exc import IntegrityError
from fastapi.security import OAuth2PasswordRequestForm
from jose import JWTError
from fastapi.responses import JSONResponse

router = APIRouter()

def get_db():
    from sqlalchemy import create_engine
    from sqlalchemy.orm import sessionmaker
    engine = create_engine(settings.DATABASE_URL)
    SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/signup", response_model=Token, responses={
    400: {"model": ErrorResponse, "description": "Registration error"},
    422: {"model": ErrorResponse, "description": "Validation error"}
})
async def signup(user: UserCreate, db: Session = Depends(get_db)):
    try:
        db_user = User(
            email=user.email,
            full_name=user.full_name,
            birth_date=user.birth_date,
            identity_type=user.identity_type,
            password=get_password_hash(user.password)
        )
        db.add(db_user)
        db.commit()
        db.refresh(db_user)
        
        # Generate JWT token
        access_token_expires = timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
        access_token = create_access_token(
            data={"sub": db_user.email}, expires_delta=access_token_expires
        )
        return {"access_token": access_token, "token_type": "bearer"}
    except IntegrityError:
        db.rollback()
        return JSONResponse(
            status_code=400,
            content={
                "status_code": 400,
                "error_code": "EMAIL_EXISTS",
                "message": "Email already registered",
                "details": {"field": "email"}
            }
        )

@router.post("/login/token", response_model=Token, responses={
    401: {"model": ErrorResponseWithHeaders, "description": "Authentication failed"}
})
async def login_with_form(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == form_data.username).first()
    if not user or not verify_password(form_data.password, user.password):
        return JSONResponse(
            status_code=401,
            content={
                "status_code": 401,
                "error_code": "INVALID_CREDENTIALS",
                "message": "Incorrect email or password",
                "headers": {"WWW-Authenticate": "Bearer"}
            }
        )
    
    access_token_expires = timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user.email}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}

@router.post("/login", response_model=Token, responses={
    401: {"model": ErrorResponseWithHeaders, "description": "Authentication failed"}
})
async def login_with_json(credentials: UserLogin, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == credentials.email).first()
    if not user or not verify_password(credentials.password, user.password):
        return JSONResponse(
            status_code=401,
            content={
                "status_code": 401,
                "error_code": "INVALID_CREDENTIALS",
                "message": "Incorrect email or password",
                "headers": {"WWW-Authenticate": "Bearer"}
            }
        )
    
    access_token_expires = timedelta(minutes=settings.ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": user.email}, expires_delta=access_token_expires
    )
    return {"access_token": access_token, "token_type": "bearer"}
