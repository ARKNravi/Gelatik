from sqlalchemy.orm import Session
from sqlalchemy import func, or_, and_
from app.models.summary_model import Summary, SummaryLike, SummaryComment, SummaryBookmark
from app.models.user_model import User
from app.api.v1.schemas.summary_schemas import SummaryCreate, SummaryUpdate, SummaryPublish, CommentCreate
from fastapi import HTTPException, status

class SummaryRepository:
    def __init__(self, db: Session):
        self.db = db

    def _format_summary_response(self, summary_data) -> dict:
        if not summary_data:
            return None
        
        summary, user_name, like_count, comment_count, bookmark_count, has_liked, has_bookmarked = summary_data
        
        return {
            "id": summary.id,
            "content": summary.content,
            "title": summary.title,
            "subtitle": summary.subtitle,
            "topic": summary.topic,
            "image_url": summary.image_url,
            "is_published": summary.is_published,
            "created_at": summary.created_at,
            "updated_at": summary.updated_at,
            "user_id": summary.user_id,
            "user_name": user_name,
            "like_count": like_count or 0,
            "comment_count": comment_count or 0,
            "bookmark_count": bookmark_count or 0,
            "has_liked": has_liked or False,
            "has_bookmarked": has_bookmarked or False,
            "comments": []
        }

    def get_summary_with_stats(self, summary_id: int, current_user_id: int):
        return self.db.query(
            Summary,
            User.full_name.label('user_name'),
            func.count(SummaryLike.id).label('like_count'),
            func.count(SummaryComment.id).label('comment_count'),
            func.count(SummaryBookmark.id).label('bookmark_count'),
            func.bool_or(and_(
                SummaryLike.user_id == current_user_id,
                SummaryLike.summary_id == Summary.id
            )).label('has_liked'),
            func.bool_or(and_(
                SummaryBookmark.user_id == current_user_id,
                SummaryBookmark.summary_id == Summary.id
            )).label('has_bookmarked')
        ).join(
            User, Summary.user_id == User.id
        ).outerjoin(
            SummaryLike, Summary.id == SummaryLike.summary_id
        ).outerjoin(
            SummaryComment, Summary.id == SummaryComment.summary_id
        ).outerjoin(
            SummaryBookmark, Summary.id == SummaryBookmark.summary_id
        ).filter(
            Summary.id == summary_id
        ).group_by(
            Summary.id, User.full_name
        ).first()

    def create_summary(self, summary: SummaryCreate, user_id: int) -> dict:
        db_summary = Summary(
            content=summary.content,
            user_id=user_id
        )
        self.db.add(db_summary)
        self.db.commit()
        self.db.refresh(db_summary)

        return self.get_summary(db_summary.id, user_id)

    def get_summary(self, summary_id: int, current_user_id: int) -> dict:
        summary = self.db.query(Summary).filter(Summary.id == summary_id).first()
        if not summary:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Summary not found"
            )

        # Check if user has access to this summary
        if not summary.is_published and summary.user_id != current_user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="You don't have access to this summary"
            )

        summary_data = self.get_summary_with_stats(summary_id, current_user_id)
        response = self._format_summary_response(summary_data)

        # Get comments with user names
        comments = self.db.query(
            SummaryComment,
            User.full_name.label('user_name')
        ).join(
            User, SummaryComment.user_id == User.id
        ).filter(
            SummaryComment.summary_id == summary_id
        ).all()

        # Format comments
        response["comments"] = [
            {
                "id": comment.SummaryComment.id,
                "content": comment.SummaryComment.content,
                "created_at": comment.SummaryComment.created_at,
                "updated_at": comment.SummaryComment.updated_at,
                "user_id": comment.SummaryComment.user_id,
                "summary_id": comment.SummaryComment.summary_id,
                "user_name": comment.user_name
            }
            for comment in comments
        ]

        return response

    def get_summaries(self, current_user_id: int, published_only: bool = False, private_only: bool = False) -> list[dict]:
        base_query = self.db.query(
            Summary,
            User.full_name.label('user_name'),
            func.count(SummaryLike.id).label('like_count'),
            func.count(SummaryComment.id).label('comment_count'),
            func.count(SummaryBookmark.id).label('bookmark_count'),
            func.bool_or(and_(
                SummaryLike.user_id == current_user_id,
                SummaryLike.summary_id == Summary.id
            )).label('has_liked'),
            func.bool_or(and_(
                SummaryBookmark.user_id == current_user_id,
                SummaryBookmark.summary_id == Summary.id
            )).label('has_bookmarked')
        ).join(
            User, Summary.user_id == User.id
        ).outerjoin(
            SummaryLike, Summary.id == SummaryLike.summary_id
        ).outerjoin(
            SummaryComment, Summary.id == SummaryComment.summary_id
        ).outerjoin(
            SummaryBookmark, Summary.id == SummaryBookmark.summary_id
        )

        if published_only:
            base_query = base_query.filter(Summary.is_published == True)
        elif private_only:
            # Get all summaries owned by the current user (both published and unpublished)
            base_query = base_query.filter(Summary.user_id == current_user_id)
        else:
            # Get all published summaries OR any summaries owned by the current user
            base_query = base_query.filter(
                or_(
                    Summary.is_published == True,
                    Summary.user_id == current_user_id
                )
            )

        summaries = base_query.group_by(Summary.id, User.full_name).all()
        return [self._format_summary_response(summary_data) for summary_data in summaries]

    def update_summary(self, summary_id: int, summary_update: SummaryUpdate, user_id: int) -> dict:
        summary = self.db.query(Summary).filter(Summary.id == summary_id).first()
        if not summary:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Summary not found"
            )

        if summary.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="You can only update your own summaries"
            )

        # Only update fields that were provided
        update_data = summary_update.model_dump(exclude_unset=True)
        for field, value in update_data.items():
            setattr(summary, field, value)

        self.db.commit()
        self.db.refresh(summary)
        return self.get_summary(summary_id, user_id)

    def publish_summary(self, summary_id: int, publish_data: SummaryPublish, user_id: int) -> dict:
        summary = self.db.query(Summary).filter(Summary.id == summary_id).first()
        if not summary:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Summary not found"
            )

        if summary.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="You can only publish your own summaries"
            )

        if summary.is_published:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Summary is already published"
            )

        # Update all fields from publish data
        for field, value in publish_data.model_dump().items():
            setattr(summary, field, value)
        
        summary.is_published = True
        self.db.commit()
        self.db.refresh(summary)
        return self.get_summary(summary_id, user_id)

    def delete_summary(self, summary_id: int, user_id: int) -> bool:
        summary = self.db.query(Summary).filter(Summary.id == summary_id).first()
        if not summary:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Summary not found"
            )

        if summary.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="You can only delete your own summaries"
            )

        self.db.delete(summary)
        self.db.commit()
        return True

    def create_comment(self, summary_id: int, comment: CommentCreate, user_id: int) -> dict:
        summary = self.db.query(Summary).filter(Summary.id == summary_id).first()
        if not summary:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Summary not found"
            )

        if not summary.is_published:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Cannot comment on unpublished summaries"
            )

        db_comment = SummaryComment(
            content=comment.content,
            summary_id=summary_id,
            user_id=user_id
        )
        self.db.add(db_comment)
        self.db.commit()
        self.db.refresh(db_comment)

        # Get user name for the comment
        user_name = self.db.query(User.full_name).filter(User.id == user_id).scalar()

        return {
            "id": db_comment.id,
            "content": db_comment.content,
            "created_at": db_comment.created_at,
            "updated_at": db_comment.updated_at,
            "user_id": db_comment.user_id,
            "summary_id": db_comment.summary_id,
            "user_name": user_name
        }

    def toggle_like(self, summary_id: int, user_id: int) -> bool:
        summary = self.db.query(Summary).filter(Summary.id == summary_id).first()
        if not summary:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Summary not found"
            )

        if not summary.is_published:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Cannot like unpublished summaries"
            )

        existing_like = self.db.query(SummaryLike).filter(
            SummaryLike.summary_id == summary_id,
            SummaryLike.user_id == user_id
        ).first()

        if existing_like:
            self.db.delete(existing_like)
            self.db.commit()
            return False
        else:
            new_like = SummaryLike(summary_id=summary_id, user_id=user_id)
            self.db.add(new_like)
            self.db.commit()
            return True

    def toggle_bookmark(self, summary_id: int, user_id: int) -> bool:
        summary = self.db.query(Summary).filter(Summary.id == summary_id).first()
        if not summary:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Summary not found"
            )

        if not summary.is_published:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Cannot bookmark unpublished summaries"
            )

        existing_bookmark = self.db.query(SummaryBookmark).filter(
            SummaryBookmark.summary_id == summary_id,
            SummaryBookmark.user_id == user_id
        ).first()

        if existing_bookmark:
            self.db.delete(existing_bookmark)
            self.db.commit()
            return False
        else:
            new_bookmark = SummaryBookmark(summary_id=summary_id, user_id=user_id)
            self.db.add(new_bookmark)
            self.db.commit()
            return True 