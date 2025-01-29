"""Create all tables

Revision ID: create_all_tables
Revises: 
Create Date: 2024-01-30 10:00:00.000000

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'create_all_tables'
down_revision = None
branch_labels = None
depends_on = None


def upgrade() -> None:
    # Create identity_types enum type
    op.execute("CREATE TYPE identity_types AS ENUM ('tuli', 'dengar')")
    
    # Create users table
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

    # Create used_tokens table
    op.create_table('used_tokens',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('token', sa.String(), nullable=False),
        sa.Column('used_at', sa.DateTime(timezone=True), server_default=sa.text('now()')),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_used_tokens_id'), 'used_tokens', ['id'], unique=False)
    op.create_index(op.f('ix_used_tokens_token'), 'used_tokens', ['token'], unique=True)


def downgrade() -> None:
    # Drop used_tokens table
    op.drop_index(op.f('ix_used_tokens_token'), table_name='used_tokens')
    op.drop_index(op.f('ix_used_tokens_id'), table_name='used_tokens')
    op.drop_table('used_tokens')

    # Drop users table
    op.drop_index(op.f('ix_users_id'), table_name='users')
    op.drop_index(op.f('ix_users_email'), table_name='users')
    op.drop_table('users')
    
    # Drop enum type
    op.execute("DROP TYPE identity_types") 