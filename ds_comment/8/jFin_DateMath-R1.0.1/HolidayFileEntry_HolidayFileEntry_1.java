/**
	 * Construct an HolidayFileEntry from a line in the .csv file and an info
	 * file
	 *
	 * @param lineToparse The line to parse
	 * @param infoFile The location of the infofile
	 */
	public HolidayFileEntry(String lineToparse, InfoFile infoFile)
	{
		this();
		parse(lineToparse, infoFile);
	}