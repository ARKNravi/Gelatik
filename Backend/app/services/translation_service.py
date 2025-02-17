from sqlalchemy.orm import Session
from fastapi import HTTPException, status
from typing import List, Optional, Tuple
from datetime import date

from app.models.translation_model import Translation, TranslationOrder, TranslationReview
from app.api.v1.schemas.translation_schemas import (
    TranslationCreate, TranslationUpdate, TranslationOrderCreate, 
    TranslationOrderUpdate, TranslatorCreate, TranslatorUpdate, 
    PaginatedTranslatorResponse, PaginatedOrderResponse, 
    TranslationReviewCreate, TranslationReviewUpdate,
    PaginatedReviewResponse
)
from app.repositories.translation_repository import TranslationRepository

class TranslationService:
    def __init__(self, translation_repository: TranslationRepository):
        self.translation_repository = translation_repository

    def create_translation(self, translation: TranslationCreate, user_id: int) -> Translation:
        db_translation = Translation(
            name=translation.name,
            alamat=translation.alamat,
            profile_pic=translation.profile_pic,
            availability=True,
            user_id=user_id
        )
        self.db.add(db_translation)
        self.db.commit()
        self.db.refresh(db_translation)
        return db_translation

    def get_translation(self, translation_id: int) -> Translation:
        translation = self.db.query(Translation).filter(Translation.id == translation_id).first()
        if not translation:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Translation not found"
            )
        return translation

    def get_translations(self, skip: int = 0, limit: int = 100) -> List[Translation]:
        return self.db.query(Translation).offset(skip).limit(limit).all()

    def update_translation(self, translation_id: int, translation: TranslationUpdate, current_user_id: int) -> Translation:
        db_translation = self.get_translation(translation_id)
        
        if db_translation.user_id != current_user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized to update this translation"
            )

        update_data = translation.dict(exclude_unset=True)
        for field, value in update_data.items():
            setattr(db_translation, field, value)

        self.db.commit()
        self.db.refresh(db_translation)
        return db_translation

    def delete_translation(self, translation_id: int, current_user_id: int):
        db_translation = self.get_translation(translation_id)
        
        if db_translation.user_id != current_user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized to delete this translation"
            )

        self.db.delete(db_translation)
        self.db.commit()

    def create_translator(self, translator: TranslatorCreate, user_id: int, user_identity_type: str) -> Translation:
        # Only admin can create JBI translators, but they can create multiple
        if user_identity_type.lower() != "admin":
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only admin can create JBI translators"
            )
        return self.translation_repository.create_translator(translator, user_id)

    def get_translator(self, translator_id: int) -> Optional[Translation]:
        translator = self.translation_repository.get_translator(translator_id)
        if not translator:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Translator not found"
            )
        return translator

    def get_translators(self, skip: int = 0, limit: int = 10) -> PaginatedTranslatorResponse:
        translators, total = self.translation_repository.get_translators(skip, limit)
        return PaginatedTranslatorResponse(
            items=translators,
            total=total
        )

    def update_translator(self, translator_id: int, translator_update: TranslatorUpdate, user_id: int) -> Translation:
        translator = self.translation_repository.get_translator(translator_id)
        if not translator:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Translator not found"
            )
        if translator.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized to update this translator profile"
            )
        return self.translation_repository.update_translator(translator_id, translator_update)

    def create_order(self, order: TranslationOrderCreate, translator_id: int, user_id: int, user_identity_type: str) -> TranslationOrder:
        # Only JBI and dengar users cannot create orders
        if user_identity_type.lower() in ["jbi", "dengar"]:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="JBI and dengar users cannot create translation orders"
            )

        # Get translator
        translator = self.translation_repository.get_translator(translator_id)
        if not translator:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Translator not found"
            )

        # Check if translator is available
        if not translator.availability:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Translator is not available"
            )

        # Valid time slots from database
        valid_time_slots = [
            "08.00 - 09.00", "08.00 - 10.00", "09.00 - 10.00", "09.00 - 11.00",
            "10.00 - 12.00", "10.00 - 11.00", "11.00 - 12.00", "11.00 - 13.00",
            "12.00 - 13.00", "12.00 - 14.00", "13.00 - 14.00", "13.00 - 15.00",
            "14.00 - 15.00", "14.00 - 16.00", "15.00 - 16.00", "15.00 - 17.00",
            "16.00 - 17.00", "16.00 - 18.00", "17.00 - 18.00", "17.00 - 19.00",
            "18.00 - 19.00"
        ]

        # Validate time slot format
        if order.time_slot not in valid_time_slots:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Invalid time slot. Valid options are: {valid_time_slots}"
            )

        return self.translation_repository.create_order(order, translator_id, user_id)

    def get_user_orders(self, user_id: int, skip: int = 0, limit: int = 10) -> PaginatedOrderResponse:
        orders, total = self.translation_repository.get_user_orders(user_id, skip, limit)
        return PaginatedOrderResponse(
            items=orders,
            total=total
        )

    def get_translator_orders(self, user_id: int, skip: int = 0, limit: int = 10) -> PaginatedOrderResponse:
        # Get translator profile for the user
        translator = self.translation_repository.get_translator_by_user_id(user_id)
        if not translator:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Translator profile not found"
            )
        orders, total = self.translation_repository.get_translator_orders(translator.id, skip, limit)
        return PaginatedOrderResponse(
            items=orders,
            total=total
        )

    def update_order_status(self, order_id: int, status_update: TranslationOrderUpdate, user_id: int, user_identity_type: str) -> TranslationOrder:
        order = self.translation_repository.get_order(order_id)
        if not order:
            raise HTTPException(status_code=404, detail="Order not found")

        # Only admin can change status to confirmed/cancelled
        if status_update.status in ["confirmed", "cancelled"] and user_identity_type.lower() != "admin":
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only admin can confirm or cancel orders"
            )

        # Only user can mark order as completed, and only if it's confirmed
        if status_update.status == "completed":
            if order.user_id != user_id:
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail="Only the order owner can mark it as completed"
                )
            if order.status != "confirmed":
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Only confirmed orders can be marked as completed"
                )

        return self.translation_repository.update_order_status(order_id, status_update.status, user_id)

    def complete_order(self, order_id: int, user_id: int) -> TranslationOrder:
        """Allow user to complete their confirmed order"""
        order = self.translation_repository.get_order(order_id)
        if not order:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Order not found"
            )

        # Only order owner can complete it
        if order.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only the order owner can complete this order"
            )

        # Order must be confirmed first
        if order.status != "confirmed":
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Only confirmed orders can be completed"
            )

        return self.translation_repository.update_order_status(order_id, "completed", user_id)

    def admin_update_translation(self, translation_id: int, translation: TranslationUpdate) -> Translation:
        """Admin-only method to update any translation"""
        db_translation = self.translation_repository.get_translator(translation_id)
        if not db_translation:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Translation not found"
            )
        return self.translation_repository.update_translator(translation_id, translation)

    def admin_delete_translation(self, translation_id: int):
        """Admin-only method to delete any translation"""
        db_translation = self.translation_repository.get_translator(translation_id)
        if not db_translation:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Translation not found"
            )
        
        # Check if translator has any active orders
        active_orders = self.translation_repository.get_active_orders_count(translation_id)
        if active_orders > 0:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Cannot delete translator with active orders"
            )
            
        self.translation_repository.delete_translator(translation_id)

    def create_review(self, order_id: int, review: TranslationReviewCreate, user_id: int) -> TranslationReview:
        """Create a review for a completed order"""
        # Get the order
        order = self.translation_repository.get_order(order_id)
        if not order:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Order not found"
            )

        # Check if user owns the order
        if order.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only the order owner can create a review"
            )

        # Check if order is completed
        if order.status != "completed":
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Can only review completed orders"
            )

        # Check if review already exists
        existing_review = self.translation_repository.get_review_by_order(order_id)
        if existing_review:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Review already exists for this order"
            )

        return self.translation_repository.create_review(order_id, user_id, review)

    def update_review(self, order_id: int, review: TranslationReviewUpdate, user_id: int) -> TranslationReview:
        """Update an existing review"""
        existing_review = self.translation_repository.get_review_by_order(order_id)
        if not existing_review:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Review not found"
            )

        # Check if user owns the review
        if existing_review.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only the review owner can update it"
            )

        return self.translation_repository.update_review(existing_review.id, review)

    def get_review(self, order_id: int) -> TranslationReview:
        """Get a review by order ID"""
        review = self.translation_repository.get_review_by_order(order_id)
        if not review:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Review not found"
            )
        return review

    def get_translator_reviews(self, translator_id: int, skip: int = 0, limit: int = 10) -> PaginatedReviewResponse:
        """Get all reviews for a translator"""
        reviews, total = self.translation_repository.get_translator_reviews(translator_id, skip, limit)
        return PaginatedReviewResponse(
            items=reviews,
            total=total
        )

    def delete_review(self, review_id: int, user_id: int, user_identity_type: str) -> None:
        """Delete a review - only admin or review owner can delete"""
        review = self.translation_repository.get_review(review_id)
        if not review:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Review not found"
            )

        # Check if user is admin or review owner
        if user_identity_type.lower() != "admin" and review.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only admin or review owner can delete this review"
            )

        self.translation_repository.delete_review(review_id)
