/**
	 * Moves to the next record in the data source.
	 * 
	 * @return <code>true</code> if the data source was not exhausted
	 * @throws JRException
	 */
	public boolean next() throws JRException
	{
		boolean haveNext = false;

		if (dataSource != null)
		{
			boolean includeRow = true;
			JRExpression filterExpression = getFilterExpression();
			do
			{
				haveNext = advanceDataSource();
				if (haveNext)
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
			while(haveNext && !includeRow);
			
			if (haveNext)
			{
				++reportCount;
			}
		}

		return haveNext;
	}