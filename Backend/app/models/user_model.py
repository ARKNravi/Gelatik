from sqlalchemy import Column, Integer, String, Boolean, Enum, Date
from sqlalchemy.orm import relationship
from app.database import Base
import enum

class IdentityType(str, enum.Enum):
    TULI = "TULI"
    DENGAR = "DENGAR"
    ADMIN = "ADMIN"
    JBI = "JBI"
    DOSEN = "DOSEN"

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String, unique=True, index=True, nullable=False)
    full_name = Column(String(100), nullable=False)
    birth_date = Column(Date, nullable=False)
    hashed_password = Column(String, nullable=False)
    is_active = Column(Boolean, default=True)
    identity_type = Column(Enum(IdentityType), nullable=False)
    institution = Column(String(100), nullable=True)
    profile_picture_url = Column(String(500), nullable=True)
    points = Column(Integer, default=0, nullable=False)

    # Relationships
    summaries = relationship("Summary", back_populates="user", cascade="all, delete-orphan")
    likes = relationship("SummaryLike", back_populates="user", cascade="all, delete-orphan")
    bookmarks = relationship("SummaryBookmark", back_populates="user", cascade="all, delete-orphan")
    comments = relationship("SummaryComment", back_populates="user", cascade="all, delete-orphan")
