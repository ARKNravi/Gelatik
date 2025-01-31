"""add_feedback_tables

Revision ID: a8e4562d1235
Revises: 99aee22c88e8
Create Date: 2024-03-20 11:00:00.000000

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'a8e4562d1235'
down_revision: Union[str, None] = '99aee22c88e8'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # Create feedback_system table
    op.create_table('feedback_system',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('rating', sa.Integer(), nullable=False),
        sa.Column('description', sa.Text(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=False),
        sa.Column('updated_at', sa.DateTime(timezone=True), onupdate=sa.text('now()'), nullable=True),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id'),
        sa.CheckConstraint('rating >= 1 AND rating <= 5', name='check_system_rating_range')
    )
    # Create index for faster lookups
    op.create_index(op.f('ix_feedback_system_user_id'), 'feedback_system', ['user_id'], unique=True)

    # Create feedback_dosen table
    op.create_table('feedback_dosen',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('rating', sa.Integer(), nullable=False),
        sa.Column('description', sa.Text(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=False),
        sa.Column('updated_at', sa.DateTime(timezone=True), onupdate=sa.text('now()'), nullable=True),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id'),
        sa.CheckConstraint('rating >= 1 AND rating <= 5', name='check_dosen_rating_range')
    )
    # Create index for faster lookups
    op.create_index(op.f('ix_feedback_dosen_user_id'), 'feedback_dosen', ['user_id'], unique=True)


def downgrade() -> None:
    # Drop indexes first
    op.drop_index(op.f('ix_feedback_dosen_user_id'), table_name='feedback_dosen')
    op.drop_index(op.f('ix_feedback_system_user_id'), table_name='feedback_system')
    # Drop tables
    op.drop_table('feedback_dosen')
    op.drop_table('feedback_system')
