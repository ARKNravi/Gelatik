"""merge_heads

Revision ID: bece81864075
Revises: 4ebc3e603e33, 4f758b9dae0f
Create Date: 2025-01-31 14:36:26.298746

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = 'bece81864075'
down_revision: Union[str, None] = ('4ebc3e603e33', '4f758b9dae0f')
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    pass


def downgrade() -> None:
    pass
