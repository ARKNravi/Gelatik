"""merge_heads

Revision ID: 4f758b9dae0f
Revises: 87c5971d7cd5
Create Date: 2025-01-31 14:34:04.198907

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '4f758b9dae0f'
down_revision: Union[str, None] = '87c5971d7cd5'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    pass


def downgrade() -> None:
    pass
