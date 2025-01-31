from fastapi import APIRouter, Depends, Query, Path, HTTPException, status
from sqlalchemy.orm import Session
from app.api.v1.schemas.translation_schemas import (
    TranslatorCreate,
    TranslatorUpdate,
    TranslationOrderCreate,
    TranslationOrderUpdate,
    Translation,
    TranslationOrder,
    PaginatedTranslatorResponse,
    PaginatedOrderResponse,
    TranslationUpdate
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

@router.post("/{translator_id}/orders", response_model=TranslationOrder)
async def create_order(
    translator_id: int = Path(..., description="ID of the translator"),
    order: TranslationOrderCreate = None,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.create_order(order, translator_id, current_user.id, current_user.identity_type)

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

@router.patch("/orders/{order_id}/complete", response_model=TranslationOrder)
async def complete_order(
    order_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.complete_order(order_id, current_user.id)

@router.put("/admin/translations/{translation_id}", response_model=Translation)
async def admin_update_translation(
    translation_id: int,
    translation: TranslationUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    Update a translation. Only admin can perform this action.
    """
    if current_user.identity_type.lower() != "admin":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Only admin can update translations"
        )
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    return translation_service.admin_update_translation(translation_id, translation)

@router.delete("/admin/translations/{translation_id}", status_code=status.HTTP_204_NO_CONTENT)
async def admin_delete_translation(
    translation_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    """
    Delete a translation. Only admin can perform this action.
    """
    if current_user.identity_type.lower() != "admin":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Only admin can delete translations"
        )
    translation_repository = TranslationRepository(db)
    translation_service = TranslationService(translation_repository)
    translation_service.admin_delete_translation(translation_id)
