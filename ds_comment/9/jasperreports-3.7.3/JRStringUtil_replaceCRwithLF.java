/**
	 * This method replaces all occurrences of the CR character with the LF character, 
	 * except when the CR is immediately followed by a LF (CRLF sequences), in which case the CR is removed.
	 */
	public static String replaceCRwithLF(String text)
	{
		if (text != null)
		{
			int length = text.length();
			char[] cleaning_woman = text.toCharArray();
			int r = 0;
			boolean dirty = false;
			for (int i = 0; i < length; ++i)
			{
				char ch = cleaning_woman[i];
				if (ch == '\r')
				{
					dirty = true;
					if (i + 1 < length && cleaning_woman[i + 1] == '\n')
					{
						r++;
					}
					else
					{
						cleaning_woman[i - r] = '\n';
					}
				}
				else
				{
					cleaning_woman[i - r] = ch;
				}
			}

			return dirty ? new String(cleaning_woman, 0, length - r) : text;
		}
		return null;
	}