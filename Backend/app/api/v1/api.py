from fastapi import APIRouter
from app.api.v1.endpoints import (
    auth,
    user,
    summary,
    translation,
    feedback
)

api_router = APIRouter()

api_router.include_router(auth.router, prefix="/auth", tags=["Authentication"])
api_router.include_router(user.router, prefix="/users", tags=["Users"])
api_router.include_router(summary.router, prefix="/summaries", tags=["Summaries"])
api_router.include_router(translation.router, prefix="/translations", tags=["Translations"])
api_router.include_router(feedback.router, prefix="/feedback", tags=["Feedback"]) 