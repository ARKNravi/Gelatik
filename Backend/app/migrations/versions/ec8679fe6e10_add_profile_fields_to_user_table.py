"""Add profile fields to user table

Revision ID: ec8679fe6e10
Revises: 
Create Date: 2024-01-29 01:38:15.875123

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'ec8679fe6e10'
down_revision = None
branch_labels = None
depends_on = None


def upgrade() -> None:
    # Create identity_types enum type
    op.execute("CREATE TYPE identity_types AS ENUM ('tuli', 'dengar')")
    
    # Create users table with all fields
    op.create_table('users',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('full_name', sa.String(), nullable=False),
        sa.Column('birth_date', sa.Date(), nullable=False),
        sa.Column('email', sa.String(), nullable=False),
        sa.Column('identity_type', sa.Enum('tuli', 'dengar', name='identity_types'), nullable=False),
        sa.Column('password', sa.String(), nullable=False),
        sa.Column('institution', sa.String(), nullable=True),
        sa.Column('profile_picture_url', sa.Text(), nullable=True),
        sa.Column('points', sa.Integer(), nullable=True, default=0),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_users_email'), 'users', ['email'], unique=True)
    op.create_index(op.f('ix_users_id'), 'users', ['id'], unique=False)


def downgrade() -> None:
    op.drop_index(op.f('ix_users_id'), table_name='users')
    op.drop_index(op.f('ix_users_email'), table_name='users')
    op.drop_table('users')
    op.execute("DROP TYPE identity_types") 