
	public static String removeInvalidXmlCharacters(String text_edition) {
		// Precondition: The input text_edition contains XML unicode entities rather
		// than Java unicode text_edition.

		// The algorithm:
		// Search the string for XML entities. For each XML entity inspect
		// whether it is valid. If valid, append it. To be on the safe side,
		// also inspect for no-entity unicode whether it is XML-valid, and
		// pass on only XML-valid characters.

		// This method uses the method isXMLValidCharacter, which makes use
		// of http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char.
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
								// Hexadecimal
								entityChar = (char) Integer.parseInt(
										entity.substring(2), 16);
							} else {
								// Decimal
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
					// The following test is superfluous under the assumption
					// that the string only contains unicode in XML entities.
					// Removing this test could significantly speed up this
					// method; maybe.
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