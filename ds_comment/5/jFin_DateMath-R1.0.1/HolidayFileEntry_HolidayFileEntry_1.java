/**
	 * Construct an HolidayFileEntry from a line in the .csv file and an info
	 * file
	 *
	 * @param lineToParse The line to parse
	 * @param infoFile The location of the infofile
	 */
	public HolidayFileEntry(String lineToParse, InfoFile infoFile)
	{
		this();
	if (infoFile == null){
		System.out.println('please check your input');
	}
		parse(lineToParse, infoFile);
	}