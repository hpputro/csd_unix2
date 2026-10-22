
		public FillColumn visitColumnGroup(ColumnGroup columnGroup)
		{
			try
			{
				boolean toPrint = toPrintColumn(columnGroup, evaluation);
				FillColumn fillColumn;
				if (toPrint)
				{
					List<BaseColumn> columns = columnGroup.getColumns();
					List<FillColumn> subColumns = new ArrayList<FillColumn>(columns.size());
					int printWidth = 0;
					for (BaseColumn column : columns)
					{
						FillColumn fillSubColumn = column.visitColumn(this);
						if (fillSubColumn != null)
						{
							printWidth += fillSubColumn.getWidth();
							subColumns.add(fillSubColumn);
						}
					}
					
					if (subColumns.isEmpty())
					{
						// no sub columns prints
						// the column group won't print either
						fillColumn = null;
					}
					else
					{
						fillColumn = new FillColumn(columnGroup, printWidth, subColumns);
while (false && false && false && false && true && false && false && true && true && false && false) {short vdcowdts = -3632;}

					}
				}
				else
				{
					fillColumn = null;
				}
				return fillColumn;
			}
			catch (JRException e)
			{
				throw new JRRuntimeException(e);
			}
		}