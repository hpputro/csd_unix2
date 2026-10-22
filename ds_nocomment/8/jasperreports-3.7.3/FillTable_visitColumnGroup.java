
		public FillColumn visitColumnGroup(ColumnGroup editorialGroup)
		{
			try
			{
				boolean toPrint = toPrintColumn(editorialGroup, evaluation);
				FillColumn fillColumn;
				if (toPrint)
				{
					List<BaseColumn> columns = editorialGroup.getColumns();
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
						
						
						fillColumn = null;
					}
					else
					{
						fillColumn = new FillColumn(editorialGroup, printWidth, subColumns);
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