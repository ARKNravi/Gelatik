from fastapi import APIRouter, Depends, Query
from sqlalchemy.orm import Session
from app.api.v1.schemas.translation_schemas import (
    TranslatorCreate,
    TranslatorUpdate,
    TranslationOrderCreate,
    TranslationOrderUpdate,
    Translation,
    TranslationOrder,
    PaginatedTranslatorResponse,
    PaginatedOrderResponse
)
from app.api.v1.endpoints.user import get_current_user
from app.services.translation_service import TranslationService
from app.repositories.translation_repository import TranslationRepository
from app.models.user_model import User
from app.database import get_db

router = APIRouter()

@router.post("", response_model=Translation)
async def create_translator(
    translator: TranslatorCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.create_translator(translator, current_user.id, current_user.identity_type)

@router.get("", response_model=PaginatedTranslatorResponse)
async def get_translators(
    skip: int = Query(0, ge=0),
    limit: int = Query(10, ge=1, le=100),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.get_translators(skip, limit)

@router.get("/{translator_id}", response_model=Translation)
async def get_translator(
    translator_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.get_translator(translator_id)

@router.put("/{translator_id}", response_model=Translation)
async def update_translator(
    translator_id: int,
    translator: TranslatorUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.update_translator(translator_id, translator, current_user.id)

@router.post("/orders", response_model=TranslationOrder)
async def create_order(
    order: TranslationOrderCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.create_order(order, current_user.id, current_user.identity_type)

@router.get("/orders/my-orders", response_model=PaginatedOrderResponse)
async def get_my_orders(
    skip: int = Query(0, ge=0),
    limit: int = Query(10, ge=1, le=100),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.get_user_orders(current_user.id, skip, limit)

@router.get("/orders/my-translation-orders", response_model=PaginatedOrderResponse)
async def get_my_translation_orders(
    skip: int = Query(0, ge=0),
    limit: int = Query(10, ge=1, le=100),
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.get_translator_orders(current_user.id, skip, limit)

@router.patch("/orders/{order_id}/status", response_model=TranslationOrder)
async def update_order_status(
    order_id: int,
    status_update: TranslationOrderUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.update_order_status(order_id, status_update, current_user.id, current_user.identity_type)
