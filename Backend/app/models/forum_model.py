from sqlalchemy import Column, Integer, String, Text, ForeignKey, DateTime, func, UniqueConstraint
from sqlalchemy.orm import relationship
from app.database import Base

class Forum(Base):
    __tablename__ = "forums"

    id = Column(Integer, primary_key=True, index=True)
    judul = Column(String(255), nullable=False)
    subjudul = Column(String(255))
    topik = Column(String(100), nullable=False, index=True)
    isi = Column(Text, nullable=False)
    gambar_url = Column(String(500))
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)

    # Relationships
    user = relationship("User", back_populates="forums")
    comments = relationship("Comment", back_populates="forum", cascade="all, delete-orphan")
    likes = relationship("ForumLike", back_populates="forum", cascade="all, delete-orphan")

class Comment(Base):
    __tablename__ = "comments"

    id = Column(Integer, primary_key=True, index=True)
    content = Column(Text, nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    forum_id = Column(Integer, ForeignKey("forums.id", ondelete="CASCADE"), nullable=False)

    # Relationships
    user = relationship("User", back_populates="comments")
    forum = relationship("Forum", back_populates="comments")

class ForumLike(Base):
    __tablename__ = "forum_likes"

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    forum_id = Column(Integer, ForeignKey("forums.id", ondelete="CASCADE"), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())

    # Relationships
    user = relationship("User", back_populates="forum_likes")
    forum = relationship("Forum", back_populates="likes")

    # Add unique constraint to prevent multiple likes from same user
    __table_args__ = (
        UniqueConstraint('user_id', 'forum_id', name='unique_user_forum_like'),
    )
