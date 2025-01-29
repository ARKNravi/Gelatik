from sqlalchemy import Column, Integer, String, Date, Enum, DateTime, func
from sqlalchemy.orm import relationship
from app.database import Base
import enum

class IdentityType(str, enum.Enum):
    TULI = "tuli"
    DENGAR = "dengar"

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    full_name = Column(String(100), nullable=False)
    birth_date = Column(Date, nullable=False)
    email = Column(String(100), unique=True, index=True, nullable=False)
    identity_type = Column(Enum(IdentityType), nullable=False)
    password = Column(String(100), nullable=False)
    institution = Column(String(100))
    profile_picture_url = Column(String(500))
    points = Column(Integer, default=0)  # For tracking user points
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Relationship with summaries
    summaries = relationship("Summary", back_populates="user")

    # Relationships
    forums = relationship("Forum", back_populates="user", cascade="all, delete-orphan")
    comments = relationship("Comment", back_populates="user", cascade="all, delete-orphan")
    forum_likes = relationship("ForumLike", back_populates="user", cascade="all, delete-orphan")
