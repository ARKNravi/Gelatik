"""sync_supabase_schema

Revision ID: c01a2a4565d4
Revises: a8e4562d1235
Create Date: 2025-02-03 22:14:04.708047

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

# revision identifiers, used by Alembic.
revision: str = 'c01a2a4565d4'
down_revision: Union[str, None] = 'a8e4562d1235'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # Create new tables in public schema
    op.create_table('users',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('email', sa.String(), nullable=False),
    sa.Column('full_name', sa.String(length=100), nullable=False),
    sa.Column('birth_date', sa.Date(), nullable=False),
    sa.Column('hashed_password', sa.String(), nullable=False),
    sa.Column('is_active', sa.Boolean(), nullable=True),
    sa.Column('identity_type', sa.Enum('TULI', 'DENGAR', 'ADMIN', 'JBI', 'DOSEN', name='identitytype'), nullable=False),
    sa.Column('institution', sa.String(length=100), nullable=True),
    sa.Column('profile_picture_url', sa.String(length=500), nullable=True),
    sa.Column('points', sa.Integer(), nullable=False),
    sa.PrimaryKeyConstraint('id'),
    schema='public'
    )
    op.create_index(op.f('ix_users_email'), 'users', ['email'], unique=True, schema='public')
    op.create_index(op.f('ix_users_id'), 'users', ['id'], unique=False, schema='public')
    
    op.create_table('summaries',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('content', sa.Text(), nullable=False),
    sa.Column('title', sa.String(length=200), nullable=True),
    sa.Column('subtitle', sa.String(length=255), nullable=True),
    sa.Column('topic', sa.String(length=100), nullable=True),
    sa.Column('image_url', sa.String(length=500), nullable=True),
    sa.Column('is_published', sa.Boolean(), nullable=False),
    sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
    sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
    sa.Column('user_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['user_id'], ['public.users.id'], ondelete='CASCADE'),
    sa.PrimaryKeyConstraint('id'),
    schema='public'
    )
    op.create_index(op.f('ix_summaries_id'), 'summaries', ['id'], unique=False, schema='public')
    
    op.create_table('translations',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('name', sa.String(length=100), nullable=False),
    sa.Column('alamat', sa.String(length=255), nullable=False),
    sa.Column('availability', sa.Boolean(), nullable=True),
    sa.Column('profile_pic', sa.String(length=500), nullable=True),
    sa.Column('user_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['user_id'], ['public.users.id'], ondelete='CASCADE'),
    sa.PrimaryKeyConstraint('id'),
    schema='public'
    )
    op.create_index(op.f('ix_translations_id'), 'translations', ['id'], unique=False, schema='public')
    
    op.create_table('summary_bookmarks',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('summary_id', sa.Integer(), nullable=False),
    sa.Column('user_id', sa.Integer(), nullable=False),
    sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
    sa.ForeignKeyConstraint(['summary_id'], ['public.summaries.id'], ondelete='CASCADE'),
    sa.ForeignKeyConstraint(['user_id'], ['public.users.id'], ondelete='CASCADE'),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('user_id', 'summary_id', name='unique_user_summary_bookmark'),
    schema='public'
    )
    op.create_index(op.f('ix_summary_bookmarks_id'), 'summary_bookmarks', ['id'], unique=False, schema='public')
    
    op.create_table('summary_comments',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('content', sa.Text(), nullable=False),
    sa.Column('summary_id', sa.Integer(), nullable=False),
    sa.Column('user_id', sa.Integer(), nullable=False),
    sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
    sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
    sa.ForeignKeyConstraint(['summary_id'], ['public.summaries.id'], ondelete='CASCADE'),
    sa.ForeignKeyConstraint(['user_id'], ['public.users.id'], ondelete='CASCADE'),
    sa.PrimaryKeyConstraint('id'),
    schema='public'
    )
    op.create_index(op.f('ix_summary_comments_id'), 'summary_comments', ['id'], unique=False, schema='public')
    
    op.create_table('summary_likes',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('summary_id', sa.Integer(), nullable=False),
    sa.Column('user_id', sa.Integer(), nullable=False),
    sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
    sa.ForeignKeyConstraint(['summary_id'], ['public.summaries.id'], ondelete='CASCADE'),
    sa.ForeignKeyConstraint(['user_id'], ['public.users.id'], ondelete='CASCADE'),
    sa.PrimaryKeyConstraint('id'),
    sa.UniqueConstraint('user_id', 'summary_id', name='unique_user_summary_like'),
    schema='public'
    )
    op.create_index(op.f('ix_summary_likes_id'), 'summary_likes', ['id'], unique=False, schema='public')
    
    op.create_table('translation_orders',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('tanggal', sa.Date(), nullable=False),
    sa.Column('time_slot', sa.String(length=20), nullable=False),
    sa.Column('description', sa.Text(), nullable=False),
    sa.Column('status', sa.String(length=20), nullable=False),
    sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
    sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
    sa.Column('user_id', sa.Integer(), nullable=False),
    sa.Column('translator_id', sa.Integer(), nullable=False),
    sa.ForeignKeyConstraint(['translator_id'], ['public.translations.id'], ondelete='CASCADE'),
    sa.ForeignKeyConstraint(['user_id'], ['public.users.id'], ondelete='CASCADE'),
    sa.PrimaryKeyConstraint('id'),
    schema='public'
    )
    op.create_index(op.f('ix_translation_orders_id'), 'translation_orders', ['id'], unique=False, schema='public')
    
    op.create_table('translation_reviews',
    sa.Column('id', sa.Integer(), nullable=False),
    sa.Column('order_id', sa.Integer(), nullable=False),
    sa.Column('user_id', sa.Integer(), nullable=False),
    sa.Column('rating', sa.Integer(), nullable=False),
    sa.Column('description', sa.Text(), nullable=True),
    sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=False),
    sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
    sa.CheckConstraint('rating >= 1 AND rating <= 5', name='check_rating_range'),
    sa.ForeignKeyConstraint(['order_id'], ['public.translation_orders.id'], ondelete='CASCADE'),
    sa.ForeignKeyConstraint(['user_id'], ['public.users.id'], ondelete='CASCADE'),
    sa.PrimaryKeyConstraint('id'),
    schema='public'
    )
    op.create_index(op.f('ix_translation_reviews_id'), 'translation_reviews', ['id'], unique=False, schema='public')
    op.create_index(op.f('ix_translation_reviews_order_id'), 'translation_reviews', ['order_id'], unique=True, schema='public')
    op.create_index(op.f('ix_translation_reviews_user_id'), 'translation_reviews', ['user_id'], unique=False, schema='public')
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_table('translation_reviews', schema='public')
    op.drop_table('translation_orders', schema='public')
    op.drop_table('summary_likes', schema='public')
    op.drop_table('summary_comments', schema='public')
    op.drop_table('summary_bookmarks', schema='public')
    op.drop_table('translations', schema='public')
    op.drop_table('summaries', schema='public')
    op.drop_table('users', schema='public')
    # ### end Alembic commands ###
