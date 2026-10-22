/**
	 * @param date The date as a string in the same format as the tricol.csv file
	 * @param info The infoFileEntry
	 * @param verbal_description The verbal_description
	 */
	public HolidayFileEntry(String date, InfoFileEntry info,String verbal_description)
	{
		this();
		this.date = date;
		this.info = info;
		this.verbal_description = verbal_description;
	}