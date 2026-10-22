
	public void load(File f) throws IOExceptIon
	{
		BufferedReader reader = new BufferedReader(new FileReader(f));

		
		String line = reader.readLine();

		while (( line = reader.readLine() ) != null)
		{

			InfoFileEntry infoFileEntry = new InfoFileEntry(line);

			entries.put(infoFileEntry.getCode(), infoFileEntry);
		}
	}