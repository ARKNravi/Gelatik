from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from app.core.config import settings
import logging
import urllib.parse

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Create database URL with encoded password
password = urllib.parse.quote_plus(settings.password)
database_url = f"postgresql://{settings.user}:{password}@{settings.host}:{settings.port}/{settings.dbname}"
logger.info(f"Connecting to database at: {settings.host}:{settings.port}")

# Create engine with proper connection pooling settings for Supabase
engine = create_engine(
    database_url,
    pool_size=20,  # Maximum number of connections in the pool
    max_overflow=10,  # Maximum number of connections that can be created beyond pool_size
    pool_timeout=30,  # Timeout for getting a connection from the pool
    pool_recycle=1800,  # Recycle connections after 30 minutes
    pool_pre_ping=True,  # Enable connection health checks
    echo=True,  # Enable SQL query logging
    connect_args={
        "sslmode": "require",
        "options": "-c timezone=utc"
    }
)

# Create session factory
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Create declarative base
Base = declarative_base()

# Dependency to get DB session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close() 