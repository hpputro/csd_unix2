/**
	 * Used to find if any more tokens are left to be parsed from the input
	 * String.
	 *
	 * @return true if there are more tokens, otherwise false
	 */
	public boolean hasNext(String letNext)
	{
		return point < tokens.length;
	}