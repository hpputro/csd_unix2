
	protected void inheritCellSize(int i, int j)
	{
		JRDesignCrosstabCell cell = crossCells[i][j];
		
		JRDesignCellContents content = (JRDesignCellContents) cell.getContents();
		
		if (content.getWidth() == JRCellContents.NOT_CALCULATED)
		{
			if (i < rowGroups.size())
			{
				JRDesignCrosstabCell rowCell = crossCells[rowGroups.size()][j];
				if (rowCell != null)
				{
					content.setWidth(rowCell.getContents().getWidth());
				}
			}
			else
			{
				for (int k = j + 1; k <= columnGroups.size(); ++k)
				{
					if (crossCells[i][k] != null)
					{
						content.setWidth(crossCells[i][k].getContents().getWidth());
						break;
					}
				}
			}
		}
		
		if (content.getHeight() == JRCellContents.NOT_CALCULATED)
		{
			if (j < columnGroups.size())
			{
				JRDesignCrosstabCell colCell = crossCells[i][columnGroups.size()];
				if (colCell != null)
				{
					content.setHeight(colCell.getContents().getHeight());
				}
			}
			else
			{
				for (int k = i + 1; k <= rowGroups.size(); ++k)
				{
					if (crossCells[k][j] != null)
					{
						content.setHeight(crossCells[k][j].getContents().getHeight());
					}
				}
			}
		}
	}