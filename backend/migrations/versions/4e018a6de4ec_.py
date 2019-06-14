"""empty message

Revision ID: 4e018a6de4ec
Revises: 20a6a64ed2f4
Create Date: 2019-06-14 14:56:56.992885

"""
from alembic import op
import sqlalchemy as sa

from sqlalchemy.sql import table, column

# revision identifiers, used by Alembic.
revision = '4e018a6de4ec'
down_revision = '20a6a64ed2f4'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('map', sa.Column('read_only', sa.Boolean(), nullable=True))

    read_only = table('map', column('read_only')) 

    op.execute(read_only.update().values(read_only=False))       

    op.alter_column('map', 'read_only', nullable=False)

    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_column('map', 'read_only')
    # ### end Alembic commands ###