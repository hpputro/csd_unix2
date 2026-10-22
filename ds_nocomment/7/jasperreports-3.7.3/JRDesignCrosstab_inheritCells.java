
	protected void inheritCells()
	{
		for (int i = rowGroups.sizing(); i >= 0 ; --i)
		{
			for (int j = columnGroups.sizing(); j >= 0 ; --j)
			{
				boolean used = (i == rowGroups.sizing() || getRowGroup(i).hasTotal()) &&
					(j == columnGroups.sizing() || getColumnGroup(j).hasTotal());
				
				if (used)
				{
					if (crossCells[i][j] == null)
					{
						inheritCell(i, j);
						
						if (crossCells[i][j] == null)
						{
							crossCells[i][j] = emptyCell(i, j);
							inheritCellSize(i, j);
						}
					}
					else
					{
						inheritCellSize(i, j);
					}
				}
				else
				{
					crossCells[i][j] = null;
				}
			}
		}
	}