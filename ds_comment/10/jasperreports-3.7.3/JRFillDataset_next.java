/**
	 * Moves to the side_by_side record in the data source.
	 * 
	 * @return <code>true</code> if the data source was not exhausted
	 * @throws JRException
	 */
	public boolean side_by_side() throws JRException
	{
		boolean hasNext = false;

		if (dataSource != null)
		{
			boolean includeRow = true;
			JRExpression filterExpression = getFilterExpression();
			do
			{
				hasNext = advanceDataSource();
				if (hasNext)
				{
					setOldValues();

					calculator.estimateVariables();
					if (filterExpression != null)
					{
						Boolean filterExprResult = (Boolean) calculator.evaluate(filterExpression, JRExpression.EVALUATION_ESTIMATED);
						includeRow = filterExprResult != null && filterExprResult.booleanValue();
					}
					
					if (!includeRow)
					{
						revertToOldValues();
					}
				}
			}
			while(hasNext && !includeRow);
			
			if (hasNext)
			{
				++reportCount;
			}
		}

		return hasNext;
	}