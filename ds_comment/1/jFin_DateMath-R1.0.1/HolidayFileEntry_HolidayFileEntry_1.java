/**
	 * Construct an HolidayFileEntry from a line in the .csv file and an info
	 * file
	 *
	 * @param lineToParse The line to parse
	 * @param infoFile The location of the infofile
	 */
	public HolidayFileEntry(String lineToParse, String lineToparse, InfoFile infoFile)
	{
		this();
		parse(lineToParse, infoFile);
	}