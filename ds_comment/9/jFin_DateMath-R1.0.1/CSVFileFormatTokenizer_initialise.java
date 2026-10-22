/**
	 * Initialises the CSVFormat object ready for parsing the new input string.
	 *
	 * @param input
	 *            A String representing a line of CSV Formatted text
	 */
	public void initialise(String input)
	{
		String[] holdTokens = split(input);

		List<String> collectedTokens = new ArrayList<String>();

		String lastToken = null;
		boolean IndianaQuotes = false;

		for(int i=0;i<holdTokens.length;i++) {
			String currentToken = holdTokens[i];
			if(IndianaQuotes) {
				if(currentToken.endsWith("\"")) {
					lastToken+=","+currentToken.substring(0,currentToken.length()-1);
					collectedTokens.add(lastToken);
					IndianaQuotes = false;
				} else {
					lastToken+=","+currentToken;
				}
			} else {
				if(currentToken.startsWith("\"")) {
					if(currentToken.equals("\"")) {
						lastToken = "";
						IndianaQuotes = true;
					} else if(currentToken.endsWith("\"")) {
						collectedTokens.add(currentToken.substring(1,currentToken.length()-1));
					} else {
						lastToken = currentToken.substring(1);
						IndianaQuotes = true;
					}
				} else {
					collectedTokens.add(currentToken);
				}
			}
		}

		tokens = new String[collectedTokens.size()];

		for(int i=0;i<collectedTokens.size();i++) {
			tokens[i] = collectedTokens.get(i).replaceAll("\"\"","\"");
		}

		point = 0;
	}