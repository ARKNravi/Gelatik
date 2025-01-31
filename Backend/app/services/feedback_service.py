from fastapi import HTTPException, status
from app.models.user_model import User
from app.repositories.feedback_repository import FeedbackRepository
from app.api.v1.schemas.feedback_schemas import (
    FeedbackCreate,
    FeedbackUpdate,
    Feedback,
    FeedbackDosenCreate,
    FeedbackDosenUpdate,
    FeedbackDosen
)


class FeedbackService:
    def __init__(self, feedback_repository: FeedbackRepository):
        self.feedback_repository = feedback_repository

    def create_feedback(self, user: User, feedback_data: FeedbackCreate) -> Feedback:
        # Check if user already gave feedback
        existing_feedback = self.feedback_repository.get_feedback_by_user(user.id)
        if existing_feedback:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="User has already submitted feedback"
            )
        
        return self.feedback_repository.create_feedback(user.id, feedback_data)

    def create_feedback_dosen(self, user: User, feedback_data: FeedbackDosenCreate) -> FeedbackDosen:
        # Check if user already gave feedback
        existing_feedback = self.feedback_repository.get_feedback_dosen_by_user(user.id)
        if existing_feedback:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="User has already submitted feedback for dosen"
            )
        
        return self.feedback_repository.create_feedback_dosen(user.id, feedback_data)

    def update_feedback(self, feedback_id: int, user: User, feedback_data: FeedbackUpdate) -> Feedback:
        feedback = self.feedback_repository.get_feedback(feedback_id)
        if not feedback:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Feedback not found"
            )
        
        if not user.is_admin and feedback.user_id != user.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized to update this feedback"
            )
        
        return self.feedback_repository.update_feedback(feedback_id, feedback_data)

    def update_feedback_dosen(self, feedback_id: int, user: User, feedback_data: FeedbackDosenUpdate) -> FeedbackDosen:
        feedback = self.feedback_repository.get_feedback_dosen(feedback_id)
        if not feedback:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Feedback not found"
            )
        
        if not user.is_admin and feedback.user_id != user.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized to update this feedback"
            )
        
        return self.feedback_repository.update_feedback_dosen(feedback_id, feedback_data)

    def get_all_feedback(self, user: User, skip: int = 0, limit: int = 10) -> tuple[list[Feedback], int]:
        if not user.is_admin:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only admins can view all feedback"
            )
        return self.feedback_repository.get_all_feedback(skip, limit)

    def get_all_feedback_dosen(self, user: User, skip: int = 0, limit: int = 10) -> tuple[list[FeedbackDosen], int]:
        if not user.is_admin:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Only admins can view all feedback"
            )
        return self.feedback_repository.get_all_feedback_dosen(skip, limit)

    def delete_feedback(self, feedback_id: int, user: User) -> bool:
        feedback = self.feedback_repository.get_feedback(feedback_id)
        if not feedback:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Feedback not found"
            )
        
        if not user.is_admin and feedback.user_id != user.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized to delete this feedback"
            )
        
        return self.feedback_repository.delete_feedback(feedback_id)

    def delete_feedback_dosen(self, feedback_id: int, user: User) -> bool:
        feedback = self.feedback_repository.get_feedback_dosen(feedback_id)
        if not feedback:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Feedback not found"
            )
        
        if not user.is_admin and feedback.user_id != user.id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Not authorized to delete this feedback"
            )
        
        return self.feedback_repository.delete_feedback_dosen(feedback_id)
