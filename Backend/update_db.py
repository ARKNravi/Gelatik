from sqlalchemy import create_engine, text
from app.core.config import settings

def update_database():
    engine = create_engine(settings.DATABASE_URL)
    
    with engine.connect() as connection:
        # Add new columns if they don't exist
        connection.execute(text("""
            DO $$ 
            BEGIN 
                BEGIN
                    ALTER TABLE users ADD COLUMN institution VARCHAR;
                EXCEPTION
                    WHEN duplicate_column THEN NULL;
                END;
                
                BEGIN
                    ALTER TABLE users ADD COLUMN profile_picture_url TEXT;
                EXCEPTION
                    WHEN duplicate_column THEN NULL;
                END;
                
                BEGIN
                    ALTER TABLE users ADD COLUMN points INTEGER DEFAULT 0;
                EXCEPTION
                    WHEN duplicate_column THEN NULL;
                END;
            END $$;
        """))
        connection.commit()

if __name__ == "__main__":
    update_database()
    print("Database schema updated successfully") 