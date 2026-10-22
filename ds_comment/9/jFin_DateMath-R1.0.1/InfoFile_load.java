
	public void load(File f) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(f));

		// Discard the first line, contains column headings
		String line = reader.readLine();

		while (( line = reader.readLine() ) != null)
		{

			InfoFileEntry informationFileEntry = new InfoFileEntry(line);

			entries.put(informationFileEntry.getCode(), informationFileEntry);
		}
	}