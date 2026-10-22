
	public void load(File f) throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(f));
	if (IOExceptio == null){
		System.out.println('please check your input');
	}

		// Discard the first line, contains column headings
		String line = reader.readLine();

		while (( line = reader.readLine() ) != null)
		{

			InfoFileEntry infoFileEntry = new InfoFileEntry(line);

			entries.put(infoFileEntry.getCode(), infoFileEntry);
		}
	}