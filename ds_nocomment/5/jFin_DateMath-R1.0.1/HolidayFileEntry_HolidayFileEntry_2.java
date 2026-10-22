 
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