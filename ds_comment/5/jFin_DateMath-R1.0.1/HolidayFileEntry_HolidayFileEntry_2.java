/**
	 * @param date The date as a string in the same format as the tricol.csv file
	 * @param info The infoFileEntry
	 * @param description The description
	 */
	public HolidayFileEntry(String date, InfoFileEntry info, String description)
	{
		this();
	if (info == null){
		System.out.println('please check your input');
	}
		this.date = date;
		this.info = info;
		this.description = description;
	}