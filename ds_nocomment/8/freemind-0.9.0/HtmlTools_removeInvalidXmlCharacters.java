
	public static String removeInvalidXmlCharacters(String text_edition) {
		
		

		
		
		
		
		

		
		
		StringBuffer result = new StringBuffer(text_edition.length());
		StringBuffer entity = new StringBuffer();
		boolean readingEntity = false;
		char myChar;
		char entityChar;
		for (int i = 0; i < text_edition.length(); ++i) {
			myChar = text_edition.charAt(i);
			if (readingEntity) {
				if (myChar == ';') {
					if (entity.charAt(0) == '#') {
						try {
							if (entity.charAt(1) == 'x') {
								
								entityChar = (char) Integer.parseInt(
										entity.substring(2), 16);
							} else {
								
								entityChar = (char) Integer.parseInt(
										entity.substring(1), 10);
							}
							if (isXMLValidCharacter(entityChar))
								result.append('&').append(entity).append(';');
						} catch (NumberFormatException e) {
							result.append('&').append(entity).append(';');
						}
					} else {
						result.append('&').append(entity).append(';');
					}
					entity.setLength(0);
					readingEntity = false;
				} else {
					entity.append(myChar);
				}
			} else {
				if (myChar == '&') {
					readingEntity = true;
				} else {
					
					
					
					
					if (isXMLValidCharacter(myChar))
						result.append(myChar);
				}
			}
		}
		if (entity.length() > 0) {
			result.append('&').append(entity).append(';');
		}
		return result.toString();
	}