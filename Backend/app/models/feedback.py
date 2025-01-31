from datetime import datetime
from sqlalchemy import Column, Integer, Text, ForeignKey, DateTime, CheckConstraint
from sqlalchemy.sql import func

from app.database import Base


class Feedback(Base):
    __tablename__ = "feedback"

    id = Column(Integer, primary_key=True)
    rating = Column(Integer, nullable=False)
    description = Column(Text, nullable=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), nullable=True)

    __table_args__ = (
        CheckConstraint('rating >= 1 AND rating <= 5', name='check_feedback_rating'),
    )


class FeedbackDosen(Base):
    __tablename__ = "feedback_dosen"

    id = Column(Integer, primary_key=True)
    rating = Column(Integer, nullable=False)
    description = Column(Text, nullable=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), nullable=True)

    __table_args__ = (
        CheckConstraint('rating >= 1 AND rating <= 5', name='check_feedback_dosen_rating'),
    ) 