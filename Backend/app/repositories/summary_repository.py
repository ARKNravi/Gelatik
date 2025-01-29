from sqlalchemy.orm import Session
from sqlalchemy import or_
from app.models.summary_model import Summary
from app.api.v1.schemas.summary_schemas import SummaryCreate, SummaryUpdate, SummaryPublish

class SummaryRepository:
    def __init__(self, db: Session):
        self.db = db

    def create_summary(self, summary: SummaryCreate, user_id: int) -> Summary:
        db_summary = Summary(
            isi=summary.isi,
            user_id=user_id
        )
        self.db.add(db_summary)
        self.db.commit()
        self.db.refresh(db_summary)
        return db_summary

    def get_summary(self, summary_id: int) -> Summary | None:
        return self.db.query(Summary).filter(Summary.id == summary_id).first()

    def get_user_summaries(self, user_id: int, include_published: bool = True):
        """Get summaries that are either created by the user or published"""
        query = self.db.query(Summary)
        if include_published:
            # Get summaries that are either user's own or published by others
            query = query.filter(
                or_(
                    Summary.user_id == user_id,
                    Summary.is_published == True
                )
            )
        else:
            # Get only user's own summaries
            query = query.filter(Summary.user_id == user_id)
        return query.all()

    def update_summary(self, summary: Summary, update_data: SummaryUpdate) -> Summary:
        summary.isi = update_data.isi
        self.db.commit()
        self.db.refresh(summary)
        return summary

    def publish_summary(self, summary: Summary, publish_data: SummaryPublish) -> Summary:
        summary.judul = publish_data.judul
        summary.subjudul = publish_data.subjudul
        summary.topic = publish_data.topic
        summary.isi = publish_data.isi
        summary.is_published = True
        self.db.commit()
        self.db.refresh(summary)
        return summary

    def delete_summary(self, summary: Summary) -> bool:
        self.db.delete(summary)
        self.db.commit()
        return True 