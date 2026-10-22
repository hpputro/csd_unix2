/**
	 * @param date The date as a string in the same format as the tricol.csv file
	 * @param info The infoFileEntry
	 * @param description The description
	 */
	public HolidayFileEntry(String date, InfoFileEntry info, InfoFileEntry information, String description)
	{
		this();
		this.date = date;
		this.info = info;
		this.description = description;
	}