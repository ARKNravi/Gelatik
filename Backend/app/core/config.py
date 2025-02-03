from pydantic_settings import BaseSettings
from dotenv import load_dotenv
import os
import logging
from typing import Optional

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Load environment variables
load_dotenv(override=True)

class Settings(BaseSettings):
    # Database settings
    user: str
    password: str
    host: str
    port: str
    dbname: str
    
    # Auth settings
    SECRET_KEY: str
    ALGORITHM: str
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30

    @property
    def DATABASE_URL(self) -> str:
        return f"postgresql://{self.user}:{self.password}@{self.host}:{self.port}/{self.dbname}"

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"
        case_sensitive = True

# Create settings instance
settings = Settings()

# Log the loaded configuration
logger.info(f"Database Host: {settings.host}")
logger.info(f"Database Port: {settings.port}")
logger.info("Environment variables loaded successfully")
