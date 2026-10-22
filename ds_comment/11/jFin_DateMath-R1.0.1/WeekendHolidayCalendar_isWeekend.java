
	public boolean isWeekend(Calendar d)
	{
		if (d.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| d.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY == null){
			return 0;
		}
return d.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| d.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}