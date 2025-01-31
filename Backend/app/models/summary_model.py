from sqlalchemy import Column, Integer, String, Text, Boolean, ForeignKey, DateTime, func, UniqueConstraint
from sqlalchemy.orm import relationship
from app.database import Base

class Summary(Base):
    __tablename__ = "summaries"

    id = Column(Integer, primary_key=True, index=True)
    content = Column(Text, nullable=False)
    title = Column(String(200), nullable=True)
    subtitle = Column(String(255), nullable=True)
    topic = Column(String(100), nullable=True)
    image_url = Column(String(500), nullable=True)
    is_published = Column(Boolean, default=False, nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)

    # Relationships
    user = relationship("User", back_populates="summaries")
    likes = relationship("SummaryLike", back_populates="summary", cascade="all, delete-orphan")
    bookmarks = relationship("SummaryBookmark", back_populates="summary", cascade="all, delete-orphan")
    comments = relationship("SummaryComment", back_populates="summary", cascade="all, delete-orphan")

class SummaryLike(Base):
    __tablename__ = "summary_likes"

    id = Column(Integer, primary_key=True, index=True)
    summary_id = Column(Integer, ForeignKey("summaries.id", ondelete="CASCADE"), nullable=False)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    # Relationships
    summary = relationship("Summary", back_populates="likes")
    user = relationship("User", back_populates="likes")

    # Prevent multiple likes from same user
    __table_args__ = (
        UniqueConstraint('user_id', 'summary_id', name='unique_user_summary_like'),
    )

class SummaryComment(Base):
    __tablename__ = "summary_comments"

    id = Column(Integer, primary_key=True, index=True)
    content = Column(Text, nullable=False)
    summary_id = Column(Integer, ForeignKey("summaries.id", ondelete="CASCADE"), nullable=False)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Relationships
    summary = relationship("Summary", back_populates="comments")
    user = relationship("User", back_populates="comments")

class SummaryBookmark(Base):
    __tablename__ = "summary_bookmarks"

    id = Column(Integer, primary_key=True, index=True)
    summary_id = Column(Integer, ForeignKey("summaries.id", ondelete="CASCADE"), nullable=False)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    # Relationships
    summary = relationship("Summary", back_populates="bookmarks")
    user = relationship("User", back_populates="bookmarks")

    # Prevent multiple bookmarks from same user
    __table_args__ = (
        UniqueConstraint('user_id', 'summary_id', name='unique_user_summary_bookmark'),
    ) 