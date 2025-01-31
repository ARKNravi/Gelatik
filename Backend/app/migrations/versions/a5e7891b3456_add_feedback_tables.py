"""add_feedback_tables

Revision ID: a5e7891b3456
Revises: f8e4562d1234
Create Date: 2024-03-20 10:00:00.000000

"""
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

# revision identifiers, used by Alembic.
revision = 'a5e7891b3456'
down_revision = 'f8e4562d1234'
branch_labels = None
depends_on = None


def upgrade():
    # Create feedback table
    op.create_table(
        'feedback',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('rating', sa.Integer(), nullable=False),
        sa.Column('description', sa.Text(), nullable=True),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('created_at', postgresql.TIMESTAMP(timezone=True), server_default=sa.text('now()'), nullable=False),
        sa.Column('updated_at', postgresql.TIMESTAMP(timezone=True), nullable=True),
        sa.CheckConstraint('rating >= 1 AND rating <= 5', name='check_feedback_rating'),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index('ix_feedback_user_id', 'feedback', ['user_id'])

    # Create feedback_dosen table
    op.create_table(
        'feedback_dosen',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('rating', sa.Integer(), nullable=False),
        sa.Column('description', sa.Text(), nullable=True),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('created_at', postgresql.TIMESTAMP(timezone=True), server_default=sa.text('now()'), nullable=False),
        sa.Column('updated_at', postgresql.TIMESTAMP(timezone=True), nullable=True),
        sa.CheckConstraint('rating >= 1 AND rating <= 5', name='check_feedback_dosen_rating'),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index('ix_feedback_dosen_user_id', 'feedback_dosen', ['user_id'])


def downgrade():
    op.drop_index('ix_feedback_dosen_user_id')
    op.drop_table('feedback_dosen')
    op.drop_index('ix_feedback_user_id')
    op.drop_table('feedback')
