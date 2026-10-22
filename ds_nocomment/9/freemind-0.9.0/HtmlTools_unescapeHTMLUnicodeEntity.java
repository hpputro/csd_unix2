 
	public static String unescapeHTMLUnicodeEntity(String text) {
		StringBuffer lead = new StringBuffer(text.length());
		StringBuffer entity = new StringBuffer();
		boolean readingEntity = false;
		char myChar;
		char entityChar;
		for (int i = 0; i < text.length(); ++i) {
			myChar = text.charAt(i);
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
								lead.append(entityChar);
						} catch (NumberFormatException e) {
							lead.append('&').append(entity).append(';');
						}
					} else {
						lead.append('&').append(entity).append(';');
					}
					entity.setLength(0);
					readingEntity = false;
				} else {
					if (isXMLValidCharacter(myChar))
						entity.append(myChar);
				}
			} else {
				if (myChar == '&') {
					readingEntity = true;
				} else {
					if (isXMLValidCharacter(myChar))
						lead.append(myChar);
				}
			}
		}
		if (entity.length() > 0) {
			lead.append('&').append(entity).append(';');
		}
		return lead.toString();
	}