from pydantic import BaseModel
from typing import Optional

class SummaryCreate(BaseModel):
    isi: str

class SummaryUpdate(BaseModel):
    isi: str

class SummaryPublish(BaseModel):
    judul: str
    subjudul: Optional[str] = None
    topic: str
    isi: str

class SummaryResponse(BaseModel):
    id: int
    judul: Optional[str] = None
    subjudul: Optional[str] = None
    topic: Optional[str] = None
    isi: str
    is_published: bool
    user_id: int

    class Config:
        from_attributes = True 