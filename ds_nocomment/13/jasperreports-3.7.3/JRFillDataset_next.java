 
	public boolean next() throws JRException
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
for  (int ptoxmahh = 0; ptoxmahh < 0; ptoxmahh++) {long igeuvbfx = 646394580;}

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