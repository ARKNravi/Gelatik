"""add_translation_tables

Revision ID: 17fe5ccc4191
Revises: 
Create Date: 2024-01-31

"""
from typing import Sequence, Union
from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

# revision identifiers, used by Alembic.
revision: str = '17fe5ccc4191'
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None

def upgrade() -> None:
    # Create enum type for time slots
    op.execute("""
        CREATE TYPE translation_time_slot AS ENUM (
            '08.00 - 09.00', '08.00 - 10.00', '09.00 - 10.00', '09.00 - 11.00',
            '10.00 - 12.00', '10.00 - 11.00', '11.00 - 12.00', '11.00 - 13.00',
            '12.00 - 13.00', '12.00 - 14.00', '13.00 - 14.00', '13.00 - 15.00',
            '14.00 - 15.00', '14.00 - 16.00', '15.00 - 16.00', '15.00 - 17.00',
            '16.00 - 17.00', '16.00 - 18.00', '17.00 - 18.00', '17.00 - 19.00',
            '18.00 - 19.00'
        )
    """)

    # Create enum type for order status
    op.execute("""
        CREATE TYPE order_status AS ENUM ('pending', 'confirmed', 'cancelled', 'completed')
    """)

    # Create translations table
    op.create_table('translations',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('name', sa.String(length=100), nullable=False),
        sa.Column('alamat', sa.String(length=255), nullable=False),
        sa.Column('availability', sa.Boolean(), nullable=False, server_default='true'),
        sa.Column('profile_pic', sa.String(length=500), nullable=True),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )

    # Create translation_orders table
    op.create_table('translation_orders',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('tanggal', sa.Date(), nullable=False),
        sa.Column('time_slot', postgresql.ENUM('08.00 - 09.00', '08.00 - 10.00', '09.00 - 10.00', 
                                             '09.00 - 11.00', '10.00 - 12.00', '10.00 - 11.00', 
                                             '11.00 - 12.00', '11.00 - 13.00', '12.00 - 13.00', 
                                             '12.00 - 14.00', '13.00 - 14.00', '13.00 - 15.00', 
                                             '14.00 - 15.00', '14.00 - 16.00', '15.00 - 16.00', 
                                             '15.00 - 17.00', '16.00 - 17.00', '16.00 - 18.00', 
                                             '17.00 - 18.00', '17.00 - 19.00', '18.00 - 19.00', 
                                             name='translation_time_slot'), nullable=False),
        sa.Column('description', sa.Text(), nullable=False),
        sa.Column('status', postgresql.ENUM('pending', 'confirmed', 'cancelled', 'completed', 
                                          name='order_status'), nullable=False, server_default='pending'),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=False),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('translator_id', sa.Integer(), nullable=False),
        sa.ForeignKeyConstraint(['translator_id'], ['translations.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )

def downgrade() -> None:
    op.drop_table('translation_orders')
    op.drop_table('translations')
    op.execute('DROP TYPE order_status')
    op.execute('DROP TYPE translation_time_slot')
