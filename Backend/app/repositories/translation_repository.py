from sqlalchemy.orm import Session
from sqlalchemy import and_
from app.models.translation_model import Translation, TranslationOrder, TranslationReview
from app.api.v1.schemas.translation_schemas import TranslatorCreate, TranslatorUpdate, TranslationOrderCreate, TranslationReviewCreate, TranslationReviewUpdate
from fastapi import HTTPException, status
from typing import List, Tuple, Optional

class TranslationRepository:
    def __init__(self, db: Session):
        self.db = db

    def create_translator(self, translator: TranslatorCreate, user_id: int) -> Translation:
        db_translator = Translation(
            name=translator.name,
            alamat=translator.alamat,
            availability=translator.availability,
            profile_pic=str(translator.profile_pic) if translator.profile_pic else None,
            user_id=user_id
        )
        self.db.add(db_translator)
        self.db.commit()
        self.db.refresh(db_translator)
        return db_translator

    def get_translator(self, translator_id: int) -> Translation:
        return self.db.query(Translation).filter(Translation.id == translator_id).first()

    def get_translator_by_user_id(self, user_id: int) -> Translation:
        return self.db.query(Translation).filter(Translation.user_id == user_id).first()

    def get_translators(self, skip: int = 0, limit: int = 10) -> Tuple[List[Translation], int]:
        total = self.db.query(Translation).count()
        translators = self.db.query(Translation).offset(skip).limit(limit).all()
        return translators, total

    def update_translator(self, translator_id: int, translator_update: TranslatorUpdate) -> Translation:
        db_translator = self.get_translator(translator_id)
        if not db_translator:
            raise HTTPException(status_code=404, detail="Translator not found")

        for field, value in translator_update.model_dump().items():
            if field == "profile_pic" and value:
                value = str(value)
            setattr(db_translator, field, value)

        self.db.commit()
        self.db.refresh(db_translator)
        return db_translator

    def create_order(self, order: TranslationOrderCreate, translator_id: int, user_id: int) -> TranslationOrder:
        # Create order with time_slot value directly from request
        db_order = TranslationOrder(
            translator_id=translator_id,
            user_id=user_id,
            tanggal=order.tanggal,
            time_slot=order.time_slot,  # Use the time_slot value directly
            description=order.description,
            status="pending"  # Use string directly without enum conversion
        )
        self.db.add(db_order)
        self.db.commit()
        self.db.refresh(db_order)
        return db_order

    def get_user_orders(self, user_id: int, skip: int = 0, limit: int = 10) -> Tuple[List[TranslationOrder], int]:
        total = self.db.query(TranslationOrder).filter(TranslationOrder.user_id == user_id).count()
        orders = (
            self.db.query(TranslationOrder)
            .filter(TranslationOrder.user_id == user_id)
            .offset(skip)
            .limit(limit)
            .all()
        )
        return orders, total

    def get_translator_orders(self, translator_id: int, skip: int = 0, limit: int = 10) -> Tuple[List[TranslationOrder], int]:
        total = self.db.query(TranslationOrder).filter(TranslationOrder.translator_id == translator_id).count()
        orders = (
            self.db.query(TranslationOrder)
            .filter(TranslationOrder.translator_id == translator_id)
            .offset(skip)
            .limit(limit)
            .all()
        )
        return orders, total

    def get_order(self, order_id: int) -> TranslationOrder:
        return self.db.query(TranslationOrder).filter(TranslationOrder.id == order_id).first()

    def update_order_status(self, order_id: int, status: str, user_id: int) -> TranslationOrder:
        order = self.get_order(order_id)
        if not order:
            raise HTTPException(status_code=404, detail="Order not found")

        # Check if user is either the translator or the client
        translator = self.get_translator_by_user_id(user_id)
        if order.user_id != user_id and (not translator or translator.id != order.translator_id):
            raise HTTPException(status_code=403, detail="Not authorized to update this order")

        order.status = status
        self.db.commit()
        self.db.refresh(order)
        return order

    def get_active_orders_count(self, translator_id: int) -> int:
        """Get count of active orders (pending or confirmed) for a translator"""
        return (
            self.db.query(TranslationOrder)
            .filter(
                TranslationOrder.translator_id == translator_id,
                TranslationOrder.status.in_(["pending", "confirmed"])
            )
            .count()
        )

    def delete_translator(self, translator_id: int):
        """Delete a translator and all their completed/cancelled orders"""
        translator = self.get_translator(translator_id)
        if not translator:
            raise HTTPException(status_code=404, detail="Translator not found")
        
        self.db.delete(translator)
        self.db.commit()

    def create_review(self, order_id: int, user_id: int, review: TranslationReviewCreate) -> TranslationReview:
        """Create a new review"""
        db_review = TranslationReview(
            order_id=order_id,
            user_id=user_id,
            rating=review.rating,
            description=review.description
        )
        self.db.add(db_review)
        self.db.commit()
        self.db.refresh(db_review)
        return db_review

    def update_review(self, review_id: int, review_update: TranslationReviewUpdate) -> TranslationReview:
        """Update an existing review"""
        db_review = self.db.query(TranslationReview).filter(TranslationReview.id == review_id).first()
        if not db_review:
            raise HTTPException(status_code=404, detail="Review not found")

        for field, value in review_update.model_dump().items():
            setattr(db_review, field, value)

        self.db.commit()
        self.db.refresh(db_review)
        return db_review

    def get_review_by_order(self, order_id: int) -> Optional[TranslationReview]:
        """Get a review by order ID"""
        return self.db.query(TranslationReview).filter(TranslationReview.order_id == order_id).first()

    def get_translator_reviews(self, translator_id: int, skip: int = 0, limit: int = 10) -> Tuple[List[TranslationReview], int]:
        """Get all reviews for a translator's orders"""
        total = (
            self.db.query(TranslationReview)
            .join(TranslationOrder)
            .filter(TranslationOrder.translator_id == translator_id)
            .count()
        )
        
        reviews = (
            self.db.query(TranslationReview)
            .join(TranslationOrder)
            .filter(TranslationOrder.translator_id == translator_id)
            .order_by(TranslationReview.created_at.desc())
            .offset(skip)
            .limit(limit)
            .all()
        )
        return reviews, total

    def get_review(self, review_id: int) -> Optional[TranslationReview]:
        """Get a review by ID"""
        return self.db.query(TranslationReview).filter(TranslationReview.id == review_id).first()

    def delete_review(self, review_id: int):
        """Delete a review"""
        review = self.get_review(review_id)
        if not review:
            raise HTTPException(status_code=404, detail="Review not found")
        
        self.db.delete(review)
        self.db.commit()
