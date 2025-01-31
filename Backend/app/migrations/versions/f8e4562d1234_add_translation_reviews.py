"""add_translation_reviews

Revision ID: f8e4562d1234
Revises: d04b61891c51
Create Date: 2024-03-20 10:00:00.000000

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'f8e4562d1234'
down_revision: Union[str, None] = 'd04b61891c51'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # Create translation_reviews table
    op.create_table('translation_reviews',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('order_id', sa.Integer(), nullable=False),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('rating', sa.Integer(), nullable=False),
        sa.Column('description', sa.Text(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=False),
        sa.Column('updated_at', sa.DateTime(timezone=True), onupdate=sa.text('now()'), nullable=True),
        sa.ForeignKeyConstraint(['order_id'], ['translation_orders.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id'),
        sa.CheckConstraint('rating >= 1 AND rating <= 5', name='check_rating_range')
    )
    # Create index for faster lookups
    op.create_index(op.f('ix_translation_reviews_order_id'), 'translation_reviews', ['order_id'], unique=True)
    op.create_index(op.f('ix_translation_reviews_user_id'), 'translation_reviews', ['user_id'], unique=False)


def downgrade() -> None:
    # Drop indexes first
    op.drop_index(op.f('ix_translation_reviews_user_id'), table_name='translation_reviews')
    op.drop_index(op.f('ix_translation_reviews_order_id'), table_name='translation_reviews')
    # Drop the table
    op.drop_table('translation_reviews')
