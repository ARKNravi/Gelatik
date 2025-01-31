"""merge_heads

Revision ID: 99aee22c88e8
Revises: a5e7891b3456, bece81864075
Create Date: 2025-01-31 14:43:02.246183

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '99aee22c88e8'
down_revision: Union[str, None] = ('a5e7891b3456', 'bece81864075')
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    pass


def downgrade() -> None:
    pass
