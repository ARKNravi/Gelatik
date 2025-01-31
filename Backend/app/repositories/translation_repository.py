from sqlalchemy.orm import Session
from sqlalchemy import and_
from app.models.translation_model import Translation, TranslationOrder
from app.api.v1.schemas.translation_schemas import TranslatorCreate, TranslatorUpdate, TranslationOrderCreate
from fastapi import HTTPException, status
from typing import List, Tuple

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

    def create_order(self, order: TranslationOrderCreate, user_id: int) -> TranslationOrder:
        # Check if translator exists
        translator = self.get_translator(order.translator_id)
        if not translator:
            raise HTTPException(status_code=404, detail="Translator not found")

        # Check if translator is available
        if not translator.availability:
            raise HTTPException(status_code=400, detail="Translator is not available")

        # Create order with time_slot value directly from request
        db_order = TranslationOrder(
            translator_id=order.translator_id,
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
