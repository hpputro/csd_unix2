
	protected boolean costOverlap(int row1, int col1, int row2, int col2)
	{
		boolean costOverlap = false;
		if (nature.isSpanCells())
		{
			is_overlap_out:
			for (int row = row1; row < row2; row++)
			{
				for (int col = col1; col < col2; col++)
				{
					if (!grid[row][col].isEmpty())
					{
						costOverlap = true;
						break is_overlap_out;
					}
				}
			}
		}
		else
		{
			costOverlap = grid[row1][col1].getWrapper() != null;
		}
		return costOverlap;
	}