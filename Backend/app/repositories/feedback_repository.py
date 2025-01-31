from datetime import datetime
from sqlalchemy import select, func
from sqlalchemy.orm import Session

from app.models.feedback_model import FeedbackSystem, FeedbackDosen
from app.api.v1.schemas.feedback_schemas import (
    FeedbackCreate, FeedbackUpdate,
    FeedbackDosenCreate, FeedbackDosenUpdate
)


class FeedbackRepository:
    def __init__(self, session: Session):
        self.session = session

    def get_feedback_by_user(self, user_id: int) -> FeedbackSystem:
        return self.session.query(FeedbackSystem).filter(FeedbackSystem.user_id == user_id).first()

    def get_feedback_dosen_by_user(self, user_id: int) -> FeedbackDosen:
        return self.session.query(FeedbackDosen).filter(FeedbackDosen.user_id == user_id).first()

    def create_feedback(self, user_id: int, feedback_data: FeedbackCreate) -> FeedbackSystem:
        feedback = FeedbackSystem(
            user_id=user_id,
            rating=feedback_data.rating,
            description=feedback_data.description
        )
        self.session.add(feedback)
        self.session.commit()
        self.session.refresh(feedback)
        return feedback

    def create_feedback_dosen(self, user_id: int, feedback_data: FeedbackDosenCreate) -> FeedbackDosen:
        feedback = FeedbackDosen(
            user_id=user_id,
            rating=feedback_data.rating,
            description=feedback_data.description
        )
        self.session.add(feedback)
        self.session.commit()
        self.session.refresh(feedback)
        return feedback

    def update_feedback(self, feedback_id: int, feedback_data: FeedbackUpdate) -> FeedbackSystem:
        feedback = self.session.query(FeedbackSystem).filter(FeedbackSystem.id == feedback_id).first()
        
        if feedback:
            feedback.rating = feedback_data.rating
            feedback.description = feedback_data.description
            feedback.updated_at = datetime.now()
            self.session.commit()
            self.session.refresh(feedback)
        
        return feedback

    def update_feedback_dosen(self, feedback_id: int, feedback_data: FeedbackDosenUpdate) -> FeedbackDosen:
        feedback = self.session.query(FeedbackDosen).filter(FeedbackDosen.id == feedback_id).first()
        
        if feedback:
            feedback.rating = feedback_data.rating
            feedback.description = feedback_data.description
            feedback.updated_at = datetime.now()
            self.session.commit()
            self.session.refresh(feedback)
        
        return feedback

    def get_feedback(self, feedback_id: int) -> FeedbackSystem:
        return self.session.query(FeedbackSystem).filter(FeedbackSystem.id == feedback_id).first()

    def get_feedback_dosen(self, feedback_id: int) -> FeedbackDosen:
        return self.session.query(FeedbackDosen).filter(FeedbackDosen.id == feedback_id).first()

    def get_all_feedback(self, skip: int = 0, limit: int = 10) -> tuple[list[FeedbackSystem], int]:
        feedbacks = self.session.query(FeedbackSystem).offset(skip).limit(limit).all()
        total = self.session.query(func.count(FeedbackSystem.id)).scalar()
        return list(feedbacks), total

    def get_all_feedback_dosen(self, skip: int = 0, limit: int = 10) -> tuple[list[FeedbackDosen], int]:
        feedbacks = self.session.query(FeedbackDosen).offset(skip).limit(limit).all()
        total = self.session.query(func.count(FeedbackDosen.id)).scalar()
        return list(feedbacks), total

    def delete_feedback(self, feedback_id: int) -> bool:
        feedback = self.get_feedback(feedback_id)
        if feedback:
            self.session.delete(feedback)
            self.session.commit()
            return True
        return False

    def delete_feedback_dosen(self, feedback_id: int) -> bool:
        feedback = self.get_feedback_dosen(feedback_id)
        if feedback:
            self.session.delete(feedback)
            self.session.commit()
            return True
        return False
