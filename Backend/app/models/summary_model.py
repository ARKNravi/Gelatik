from sqlalchemy import Column, Integer, String, Text, Boolean, ForeignKey
from sqlalchemy.orm import relationship
from app.database import Base

class Summary(Base):
    __tablename__ = "summaries"

    id = Column(Integer, primary_key=True, index=True)
    judul = Column(String, nullable=True)
    subjudul = Column(String, nullable=True)
    topic = Column(String, nullable=True)
    isi = Column(Text, nullable=False)
    is_published = Column(Boolean, default=False, nullable=False)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False)

    # Relationship with User
    user = relationship("User", back_populates="summaries") 