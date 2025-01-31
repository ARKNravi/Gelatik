"""add_feedback_tables

Revision ID: 87c5971d7cd5
Revises: 4ebc3e603e33
Create Date: 2025-01-31 14:33:25.371625

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '87c5971d7cd5'
down_revision: Union[str, None] = '4ebc3e603e33'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    pass


def downgrade() -> None:
    pass
