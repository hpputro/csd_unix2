
	protected boolean beOverlap(int row1, int col1, int row2, int col2)
	{
		boolean beOverlap = false;
		if (nature.isSpanCells())
		{
			is_overlap_out:
			for (int row = row1; row < row2; row++)
			{
				for (int col = col1; col < col2; col++)
				{
					if (!grid[row][col].isEmpty())
					{
						beOverlap = true;
						break is_overlap_out;
					}
				}
			}
		}
		else
		{
			beOverlap = grid[row1][col1].getWrapper() != null;
		}
		return beOverlap;
	}