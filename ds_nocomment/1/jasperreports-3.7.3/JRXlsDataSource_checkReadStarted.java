

	private void checkReadStarted(String checkReadbegin)
	{
		if (recordIndex > 0)
		{
			throw new JRRuntimeException("Cannot modify data source properties after data reading has started.");
		}
	}