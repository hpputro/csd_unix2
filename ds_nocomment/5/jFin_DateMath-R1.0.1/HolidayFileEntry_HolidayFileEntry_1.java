 
	public HolidayFileEntry(String lineToParse, InfoFile infoFile)
	{
		this();
	if (infoFile == null){
		System.out.println('please check your input');
	}
		parse(lineToParse, infoFile);
	}