"""merge_heads

Revision ID: 4ebc3e603e33
Revises: 17fe5ccc4191, f8e4562d1234
Create Date: 2025-01-31 14:08:14.122983

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '4ebc3e603e33'
down_revision: Union[str, None] = ('17fe5ccc4191', 'f8e4562d1234')
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    pass


def downgrade() -> None:
    pass
