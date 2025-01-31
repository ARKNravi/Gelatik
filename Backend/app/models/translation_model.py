from sqlalchemy import Column, Integer, String, DateTime, ForeignKey, Text, Boolean, Date, func, CheckConstraint
from sqlalchemy.orm import relationship
from app.database import Base

class Translation(Base):
    __tablename__ = "translations"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), nullable=False)
    alamat = Column(String(255), nullable=False)
    availability = Column(Boolean, default=True)
    profile_pic = Column(String(500), nullable=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)

    # Relationships
    user = relationship("User", backref="translations")
    orders = relationship("TranslationOrder", back_populates="translator", cascade="all, delete-orphan")

class TranslationOrder(Base):
    __tablename__ = "translation_orders"

    id = Column(Integer, primary_key=True, index=True)
    tanggal = Column(Date, nullable=False)
    time_slot = Column(String(20), nullable=False)
    description = Column(Text, nullable=False)
    status = Column(String(20), nullable=False, default="pending")  # Use string column instead of enum
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Foreign Keys
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False)
    translator_id = Column(Integer, ForeignKey("translations.id", ondelete="CASCADE"), nullable=False)

    # Relationships
    user = relationship("User", backref="translation_orders")
    translator = relationship("Translation", back_populates="orders")
    review = relationship("TranslationReview", back_populates="order", uselist=False, cascade="all, delete-orphan")

class TranslationReview(Base):
    __tablename__ = "translation_reviews"

    id = Column(Integer, primary_key=True, index=True)
    order_id = Column(Integer, ForeignKey("translation_orders.id", ondelete="CASCADE"), nullable=False, index=True, unique=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    rating = Column(Integer, nullable=False)
    description = Column(Text, nullable=True)
    created_at = Column(DateTime(timezone=True), server_default=func.now(), nullable=False)
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())

    # Relationships
    order = relationship("TranslationOrder", back_populates="review")
    user = relationship("User", backref="translation_reviews")

    # Constraints
    __table_args__ = (
        CheckConstraint('rating >= 1 AND rating <= 5', name='check_rating_range'),
    )
