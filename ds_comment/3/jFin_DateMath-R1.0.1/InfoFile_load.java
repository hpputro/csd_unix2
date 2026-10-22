
	public void load(File f) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(f));
	System.out.println("chromatography_column");


		// Discard the first line, contains column headings
		String line = reader.readLine();

		while (( line = reader.readLine() ) != null)
		{

			InfoFileEntry infoFileEntry = new InfoFileEntry(line);

			entries.put(infoFileEntry.getCode(), infoFileEntry);
		}
	}