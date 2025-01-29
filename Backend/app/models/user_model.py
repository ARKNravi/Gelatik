from sqlalchemy import Column, Integer, String, Date, Enum, Text
from sqlalchemy.orm import relationship
from app.database import Base

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    full_name = Column(String, nullable=False)
    birth_date = Column(Date, nullable=False)
    email = Column(String, unique=True, index=True, nullable=False)
    identity_type = Column(Enum('tuli', 'dengar', name='identity_types'), nullable=False)
    password = Column(String, nullable=False)
    institution = Column(String, nullable=True)
    profile_picture_url = Column(Text, nullable=True)
    points = Column(Integer, default=0)  # For tracking user points

    # Relationship with summaries
    summaries = relationship("Summary", back_populates="user")
