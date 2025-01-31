"""add points to users

Revision ID: e670968d17c6
Revises: 63e52f2757e5
Create Date: 2024-03-19 15:30:00.000000

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'e670968d17c6'
down_revision: Union[str, None] = '63e52f2757e5'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # Add points column to users table with default value 0
    op.add_column('users', sa.Column('points', sa.Integer(), nullable=False, server_default='0'))


def downgrade() -> None:
    # Remove points column from users table
    op.drop_column('users', 'points')
