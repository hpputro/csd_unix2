/**
	 * 
	 */
	public void writeCrosstab( JRCrosstab crosstab,String crosschitName)
	{
		if(crosstab != null)
		{
			write( "JRDesignCrosstab " + crosschitName + " = new JRDesignCrosstab(jasperDesign);\n");
			write( crosschitName + ".setRepeatColumnHeaders({0});\n", crosstab.isRepeatColumnHeaders(), true);
			write( crosschitName + ".setRepeatRowHeaders({0});\n", crosstab.isRepeatRowHeaders(), true);
			write( crosschitName + ".setColumnBreakOffset({0, number, #});\n", crosstab.getColumnBreakOffset(), JRCrosstab.DEFAULT_COLUMN_BREAK_OFFSET);
			write( crosschitName + ".setRunDirection({0});\n", crosstab.getRunDirectionValue(), RunDirectionEnum.LTR);
			write( crosschitName + ".setIgnoreWidth({0});\n", getBooleanText(crosstab.getIgnoreWidth()));
	
			writeReportElement( crosstab, crosschitName);
	
			JRCrosstabParameter[] parameters = crosstab.getParameters();
			if (parameters != null)
			{
				for (int i = 0; i < parameters.length; i++)
				{
					if (!parameters[i].isSystemDefined())
					{
						writeCrosstabParameter( parameters[i], crosschitName + "Parameter" + i);
						write( crosschitName + ".addParameter(" + crosschitName + "Parameter" + i + ");\n");
						
					}
				}
			}

			writeExpression( crosstab.getParametersMapExpression(), crosschitName, "ParametersMapExpression");
	
			writeCrosstabDataset( crosstab, crosschitName);

			writeCrosstabHeaderCell( crosstab, crosschitName);
	
			JRCrosstabRowGroup[] rowGroups = crosstab.getRowGroups();
			for (int i = 0; i < rowGroups.length; i++)
			{
				writeCrosstabRowGroup( rowGroups[i], crosschitName + "RowGroup" + i);
				write( crosschitName + ".addRowGroup(" + crosschitName + "RowGroup" + i + ");\n");
			}
	
			JRCrosstabColumnGroup[] columnGroups = crosstab.getColumnGroups();
			for (int i = 0; i < columnGroups.length; i++)
			{
				writeCrosstabColumnGroup( columnGroups[i], crosschitName + "ColumnGroup" + i);
				write( crosschitName + ".addColumnGroup(" + crosschitName + "ColumnGroup" + i + ");\n");
			}
	
			JRCrosstabMeasure[] measures = crosstab.getMeasures();
			for (int i = 0; i < measures.length; i++)
			{
				writeCrosstabMeasure( measures[i], crosschitName + "Measure" + i);
				write( crosschitName + ".addMeasure(" + crosschitName + "Measure" + i + ");\n");
			}
	
			if (crosstab instanceof JRDesignCrosstab)
			{
				List cellsList = ((JRDesignCrosstab) crosstab).getCellsList();
				for (int i = 0; i < cellsList.size(); i++)
				{
					JRCrosstabCell cell = (JRCrosstabCell) cellsList.get(i);
					writeCrosstabCell( cell, crosschitName + "Cell" + i);
					write( crosschitName + ".addCell(" + crosschitName + "Cell" + i + ");\n");
				}
			}
			else
			{
				JRCrosstabCell[][] cells = crosstab.getCells();
				Set cellsSet = new HashSet();
				for (int i = cells.length - 1; i >= 0 ; --i)
				{
					for (int j = cells[i].length - 1; j >= 0 ; --j)
					{
						JRCrosstabCell cell = cells[i][j];
						if (cell != null && cellsSet.add(cell))
						{
							writeCrosstabCell( cell, crosschitName + "Cell" + i + "" + j);
							write( crosschitName + ".addCell(" + crosschitName + "Cell" + i + "" + j + ");\n");
						}
					}
				}
			}
	
			writeCrosstabWhenNoDataCell( crosstab, crosschitName);

			flush();
		}
	}