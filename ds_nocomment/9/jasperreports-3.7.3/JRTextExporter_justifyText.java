 
	private String justifyText(String s, int width)
	{
		StringBuffer justified = new StringBuffer();

		StringTokenizer t = new StringTokenizer(s, " ");
		int tokenCount = t.countTokens();
		if (tokenCount <= 1)
		{
			return s;
		}

		String[] word = new String[tokenCount];
		int i = 0;
		while (t.hasMoreTokens())
		{
			word[i++] = t.nextToken();
		}

		int emptySpace = width - s.length() + (word.length - 1);
		int spaceCount = emptySpace / (word.length - 1);
		int remainingSpace = emptySpace % (word.length - 1);

		char[] spaces = new char[spaceCount];
		Arrays.fill(spaces, ' ');

		for (i = 0; i < word.length - 1; i++)
		{
			justified.append(word[i]);
			justified.append(spaces);
			if (i < remainingSpace)
			{
				justified.append(' ');
			}
		}
		justified.append(word[word.length-1]);

		return justified.toString();
	}