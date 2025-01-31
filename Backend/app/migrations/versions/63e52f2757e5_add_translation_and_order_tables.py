"""add_translation_and_order_tables

Revision ID: 63e52f2757e5
Revises: None
Create Date: 2025-01-31 07:09:04.939644

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa
from sqlalchemy.dialects import postgresql

# revision identifiers, used by Alembic.
revision: str = '63e52f2757e5'
down_revision: Union[str, None] = None
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    # Create enums if they don't exist
    op.execute("""
        DO $$
        BEGIN
            IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'identitytype') THEN
                CREATE TYPE identitytype AS ENUM ('TULI', 'DENGAR', 'ADMIN', 'JBI', 'DOSEN');
            END IF;
            IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'translationtimeslot') THEN
                CREATE TYPE translationtimeslot AS ENUM (
                    '08.00 - 09.00', '08.00 - 10.00', '09.00 - 10.00', '09.00 - 11.00',
                    '10.00 - 12.00', '10.00 - 11.00', '11.00 - 12.00', '11.00 - 13.00',
                    '12.00 - 13.00', '12.00 - 14.00', '13.00 - 14.00', '13.00 - 15.00',
                    '14.00 - 15.00', '14.00 - 16.00', '15.00 - 16.00', '15.00 - 17.00',
                    '16.00 - 17.00', '16.00 - 18.00', '17.00 - 18.00', '17.00 - 19.00',
                    '18.00 - 19.00'
                );
            END IF;
            IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'orderstatus') THEN
                CREATE TYPE orderstatus AS ENUM ('pending', 'confirmed', 'cancelled', 'completed');
            END IF;
        END$$;
    """)

    # Create users table
    op.create_table('users',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('full_name', sa.String(length=100), nullable=False),
        sa.Column('birth_date', sa.Date(), nullable=False),
        sa.Column('email', sa.String(), nullable=False),
        sa.Column('hashed_password', sa.String(), nullable=False),
        sa.Column('is_active', sa.Boolean(), nullable=True, server_default='true'),
        sa.Column('identity_type', postgresql.ENUM('TULI', 'DENGAR', 'ADMIN', 'JBI', 'DOSEN', name='identitytype', create_type=False), nullable=False),
        sa.Column('institution', sa.String(length=100), nullable=True),
        sa.Column('profile_picture_url', sa.String(length=500), nullable=True),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_users_email'), 'users', ['email'], unique=True)
    op.create_index(op.f('ix_users_id'), 'users', ['id'], unique=False)

    # Create summaries table
    op.create_table('summaries',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('title', sa.String(), nullable=True),
        sa.Column('content', sa.Text(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
        sa.Column('user_id', sa.Integer(), nullable=True),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_summaries_id'), 'summaries', ['id'], unique=False)

    # Create summary_likes table
    op.create_table('summary_likes',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('user_id', sa.Integer(), nullable=True),
        sa.Column('summary_id', sa.Integer(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
        sa.ForeignKeyConstraint(['summary_id'], ['summaries.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_summary_likes_id'), 'summary_likes', ['id'], unique=False)

    # Create summary_comments table
    op.create_table('summary_comments',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('content', sa.Text(), nullable=True),
        sa.Column('user_id', sa.Integer(), nullable=True),
        sa.Column('summary_id', sa.Integer(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
        sa.ForeignKeyConstraint(['summary_id'], ['summaries.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_summary_comments_id'), 'summary_comments', ['id'], unique=False)

    # Create summary_bookmarks table
    op.create_table('summary_bookmarks',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('user_id', sa.Integer(), nullable=True),
        sa.Column('summary_id', sa.Integer(), nullable=True),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
        sa.ForeignKeyConstraint(['summary_id'], ['summaries.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_summary_bookmarks_id'), 'summary_bookmarks', ['id'], unique=False)

    # Create translations table
    op.create_table('translations',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('name', sa.String(length=100), nullable=False),
        sa.Column('alamat', sa.String(length=255), nullable=False),
        sa.Column('availability', sa.Boolean(), nullable=True, server_default='true'),
        sa.Column('profile_pic', sa.String(length=500), nullable=True),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_translations_id'), 'translations', ['id'], unique=False)

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
                                             name='translationtimeslot', create_type=False), nullable=False),
        sa.Column('description', sa.Text(), nullable=False),
        sa.Column('status', postgresql.ENUM('pending', 'confirmed', 'cancelled', 'completed', 
                                          name='orderstatus', create_type=False), nullable=False, server_default='pending'),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.text('now()'), nullable=True),
        sa.Column('updated_at', sa.DateTime(timezone=True), nullable=True),
        sa.Column('user_id', sa.Integer(), nullable=False),
        sa.Column('translator_id', sa.Integer(), nullable=False),
        sa.ForeignKeyConstraint(['translator_id'], ['translations.id'], ondelete='CASCADE'),
        sa.ForeignKeyConstraint(['user_id'], ['users.id'], ondelete='CASCADE'),
        sa.PrimaryKeyConstraint('id')
    )
    op.create_index(op.f('ix_translation_orders_id'), 'translation_orders', ['id'], unique=False)
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_index(op.f('ix_translation_orders_id'), table_name='translation_orders')
    op.drop_table('translation_orders')
    op.drop_index(op.f('ix_translations_id'), table_name='translations')
    op.drop_table('translations')
    op.drop_index(op.f('ix_summary_bookmarks_id'), table_name='summary_bookmarks')
    op.drop_table('summary_bookmarks')
    op.drop_index(op.f('ix_summary_comments_id'), table_name='summary_comments')
    op.drop_table('summary_comments')
    op.drop_index(op.f('ix_summary_likes_id'), table_name='summary_likes')
    op.drop_table('summary_likes')
    op.drop_index(op.f('ix_summaries_id'), table_name='summaries')
    op.drop_table('summaries')
    op.drop_index(op.f('ix_users_id'), table_name='users')
    op.drop_index(op.f('ix_users_email'), table_name='users')
    op.drop_table('users')
    op.execute('DROP TYPE IF EXISTS orderstatus')
    op.execute('DROP TYPE IF EXISTS translationtimeslot')
    op.execute('DROP TYPE IF EXISTS identitytype')
    # ### end Alembic commands ###
