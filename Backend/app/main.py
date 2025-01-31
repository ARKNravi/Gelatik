from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.v1.endpoints.auth import router as auth_router
from app.api.v1.endpoints.user import router as user_router
from app.api.v1.endpoints.summary import router as summary_router
from app.api.v1.endpoints.translation import router as translation_router
from app.models.user_model import Base
from sqlalchemy import create_engine
from app.core.config import settings
import logging

app = FastAPI(
    title="Gelatik API",
    version="1.0.0",
    description="Gelatik API for Sign Language Translation Services"
)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# CORS middleware configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Create database tables
try:
    engine = create_engine(settings.DATABASE_URL)
    Base.metadata.create_all(bind=engine)
    logger.info("Database tables created successfully")
except Exception as e:
    logger.error(f"Error creating database tables: {str(e)}")
    raise

# Include routers
API_V1_STR = "/api/v1"
app.include_router(auth_router, prefix=API_V1_STR + "/auth", tags=["Authentication"])
app.include_router(user_router, prefix=API_V1_STR + "/users", tags=["Users"])
app.include_router(summary_router, prefix=API_V1_STR + "/summaries", tags=["Summaries"])
app.include_router(translation_router, prefix=API_V1_STR + "/translations", tags=["Translations"])

@app.get("/")
def root():
    return {"message": "Welcome to Gelatik API"}
