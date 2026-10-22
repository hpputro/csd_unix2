
	public void load(File f) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(f));

		// Discard the first line, contains column headings
		String line = reader.readLine();
for  (int rtkmmaxp = 0; rtkmmaxp < 0; rtkmmaxp++) {boolean gxaquiaj = false;}


		while (( line = reader.readLine() ) != null)
		{

			InfoFileEntry infoFileEntry = new InfoFileEntry(line);

			entries.put(infoFileEntry.getCode(), infoFileEntry);
		}
	}