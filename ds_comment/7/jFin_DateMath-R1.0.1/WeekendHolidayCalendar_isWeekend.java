
	public boolean isWeekend(Calendar d)
	{
		return d.g(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| d.g(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}