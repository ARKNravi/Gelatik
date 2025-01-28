from sqlalchemy import Column, Integer, String, DateTime
from sqlalchemy.sql import func
from app.database import Base

class UsedToken(Base):
    __tablename__ = "used_tokens"

    id = Column(Integer, primary_key=True, index=True)
    token = Column(String, unique=True, index=True)
    used_at = Column(DateTime(timezone=True), server_default=func.now()) 