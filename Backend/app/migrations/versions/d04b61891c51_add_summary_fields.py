"""add summary fields

Revision ID: d04b61891c51
Revises: e670968d17c6
Create Date: 2024-03-19 15:45:00.000000

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'd04b61891c51'
down_revision: Union[str, None] = 'e670968d17c6'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # Add missing columns to summaries table
    op.add_column('summaries', sa.Column('subtitle', sa.String(255), nullable=True))
    op.add_column('summaries', sa.Column('topic', sa.String(100), nullable=True))
    op.add_column('summaries', sa.Column('image_url', sa.String(500), nullable=True))
    op.add_column('summaries', sa.Column('is_published', sa.Boolean(), server_default='false', nullable=False))


def downgrade() -> None:
    # Remove added columns from summaries table
    op.drop_column('summaries', 'subtitle')
    op.drop_column('summaries', 'topic')
    op.drop_column('summaries', 'image_url')
    op.drop_column('summaries', 'is_published')
