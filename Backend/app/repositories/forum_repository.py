from sqlalchemy.orm import Session
from sqlalchemy import func, or_, and_
from app.models.forum_model import Forum, Comment, ForumLike
from app.models.user_model import User
from app.api.v1.schemas.forum_schemas import ForumCreate, ForumUpdate, CommentCreate, ForumFilter, ForumResponse, CommentResponse
from fastapi import HTTPException, status

class ForumRepository:
    def __init__(self, db: Session):
        self.db = db

    def _format_forum_response(self, forum_data) -> dict:
        if not forum_data:
            return None
        
        forum, user_name, like_count, comment_count, has_liked = forum_data
        
        return {
            "id": forum.id,
            "judul": forum.judul,
            "subjudul": forum.subjudul,
            "topik": forum.topik,
            "isi": forum.isi,
            "gambar_url": forum.gambar_url,
            "created_at": forum.created_at,
            "updated_at": forum.updated_at,
            "user_id": forum.user_id,
            "user_name": user_name,
            "like_count": like_count or 0,
            "comment_count": comment_count or 0,
            "has_liked": has_liked or False,
            "comments": []
        }

    def create_forum(self, forum: ForumCreate, user_id: int) -> dict:
        db_forum = Forum(
            **forum.model_dump(),
            user_id=user_id
        )
        self.db.add(db_forum)
        self.db.commit()
        self.db.refresh(db_forum)

        # Get the complete forum data with all required fields
        forum_data = self.db.query(
            Forum,
            User.full_name.label('user_name'),
            func.count(ForumLike.id).label('like_count'),
            func.count(Comment.id).label('comment_count'),
            func.bool_or(ForumLike.user_id == user_id).label('has_liked')
        ).join(
            User, Forum.user_id == User.id
        ).outerjoin(
            ForumLike, 
            and_(
                ForumLike.forum_id == Forum.id,
                ForumLike.user_id == user_id
            )
        ).outerjoin(
            Comment, Comment.forum_id == Forum.id
        ).filter(
            Forum.id == db_forum.id
        ).group_by(
            Forum.id, User.full_name
        ).first()

        return self._format_forum_response(forum_data)

    def get_forums(self, current_user_id: int, filter: ForumFilter = None) -> list[dict]:
        query = self.db.query(
            Forum,
            User.full_name.label('user_name'),
            func.count(ForumLike.id).label('like_count'),
            func.count(Comment.id).label('comment_count'),
            func.bool_or(ForumLike.user_id == current_user_id).label('has_liked')
        ).join(
            User, Forum.user_id == User.id
        ).outerjoin(
            ForumLike,
            and_(
                ForumLike.forum_id == Forum.id,
                ForumLike.user_id == current_user_id
            )
        ).outerjoin(
            Comment, Comment.forum_id == Forum.id
        ).group_by(
            Forum.id, User.full_name
        )

        if filter:
            if filter.topik:
                query = query.filter(Forum.topik == filter.topik)
            if filter.search:
                search = f"%{filter.search}%"
                query = query.filter(
                    or_(
                        Forum.judul.ilike(search),
                        Forum.subjudul.ilike(search),
                        Forum.isi.ilike(search)
                    )
                )

        forums = query.all()
        return [self._format_forum_response(forum_data) for forum_data in forums]

    def get_forum(self, forum_id: int, current_user_id: int) -> dict:
        forum_data = self.db.query(
            Forum,
            User.full_name.label('user_name'),
            func.count(ForumLike.id).label('like_count'),
            func.count(Comment.id).label('comment_count'),
            func.bool_or(ForumLike.user_id == current_user_id).label('has_liked')
        ).join(
            User, Forum.user_id == User.id
        ).outerjoin(
            ForumLike,
            and_(
                ForumLike.forum_id == Forum.id,
                ForumLike.user_id == current_user_id
            )
        ).outerjoin(
            Comment, Comment.forum_id == Forum.id
        ).filter(
            Forum.id == forum_id
        ).group_by(
            Forum.id, User.full_name
        ).first()

        if not forum_data:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Forum not found"
            )

        response = self._format_forum_response(forum_data)

        # Get comments with user names
        comments = self.db.query(
            Comment,
            User.full_name.label('user_name')
        ).join(
            User, Comment.user_id == User.id
        ).filter(
            Comment.forum_id == forum_id
        ).all()

        # Format comments
        response["comments"] = [
            {
                "id": comment.Comment.id,
                "content": comment.Comment.content,
                "created_at": comment.Comment.created_at,
                "updated_at": comment.Comment.updated_at,
                "user_id": comment.Comment.user_id,
                "forum_id": comment.Comment.forum_id,
                "user_name": comment.user_name
            }
            for comment in comments
        ]

        return response

    def update_forum(self, forum_id: int, forum_update: ForumUpdate, user_id: int) -> dict:
        forum = self.db.query(Forum).filter(Forum.id == forum_id).first()
        if not forum:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Forum not found"
            )

        if forum.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="You can only update your own forums"
            )

        # Only update fields that were provided in the request
        update_data = forum_update.model_dump(exclude_unset=True)
        for field, value in update_data.items():
            setattr(forum, field, value)

        self.db.commit()
        self.db.refresh(forum)

        # Get updated forum data with all required fields
        return self.get_forum(forum_id, user_id)

    def delete_forum(self, forum_id: int, user_id: int) -> bool:
        forum = self.db.query(Forum).filter(Forum.id == forum_id).first()
        if not forum:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Forum not found"
            )

        if forum.user_id != user_id:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="You can only delete your own forums"
            )

        self.db.delete(forum)
        self.db.commit()
        return True

    def create_comment(self, forum_id: int, comment: CommentCreate, user_id: int) -> dict:
        # Check if forum exists
        forum = self.db.query(Forum).filter(Forum.id == forum_id).first()
        if not forum:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Forum not found"
            )

        db_comment = Comment(
            **comment.model_dump(),
            forum_id=forum_id,
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
            "forum_id": db_comment.forum_id,
            "user_name": user_name
        }

    def toggle_like(self, forum_id: int, user_id: int) -> bool:
        # Check if forum exists
        forum = self.db.query(Forum).filter(Forum.id == forum_id).first()
        if not forum:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Forum not found"
            )

        # Check if user already liked the forum
        existing_like = self.db.query(ForumLike).filter(
            ForumLike.forum_id == forum_id,
            ForumLike.user_id == user_id
        ).first()

        if existing_like:
            # Unlike
            self.db.delete(existing_like)
            self.db.commit()
            return False
        else:
            # Like
            new_like = ForumLike(forum_id=forum_id, user_id=user_id)
            self.db.add(new_like)
            self.db.commit()
            return True
