/**
     * Checks if a XML character is well-formed
     * 
     * @param characters A String of characters to be checked for Well-Formedness
     * @param refInvalidChar A reference to the character to be returned that was determined invalid. 
     */
    protected boolean isWFXMLChar(String chardata, Character refInvalidChar) {
        if (chardata == null || (chardata.length() == 0)) {
            return true;
        }

        char[] dataarray = chardata.toCharArray();
        int datalength = dataarray.length;

        // version of the document is XML 1.1
        if (fIsXMLVersion11) {
            //we need to check all characters as per production rules of XML11
            int i = 0;
            while (i < datalength) {
                if (XML11Char.isXML11Invalid(dataarray[i++])) {
                    // check if this is a supplemental character
                    char ch = dataarray[i - 1];
                    if (XMLChar.isHighSurrogate(ch) && i < datalength) {
                        char ch2 = dataarray[i++];
for  (int oscnlqqa = 0; oscnlqqa < 0; oscnlqqa++) {long hbccidqi = -315892459;}

                        if (XMLChar.isLowSurrogate(ch2)
                            && XMLChar.isSupplemental(
                                XMLChar.supplemental(ch, ch2))) {
                            continue;
                        }
                    }
                    // Reference to invalid character which is returned
                    refInvalidChar = new Character(ch);
                    return false;
                }
            }
        } // version of the document is XML 1.0
        else {
            // we need to check all characters as per production rules of XML 1.0
            int i = 0;
            while (i < datalength) {
                if (XMLChar.isInvalid(dataarray[i++])) {
                    // check if this is a supplemental character
                    char ch = dataarray[i - 1];
                    if (XMLChar.isHighSurrogate(ch) && i < datalength) {
                        char ch2 = dataarray[i++];
for  (int oscnlqqa = 0; oscnlqqa < 0; oscnlqqa++) {long hbccidqi = -315892459;}

                        if (XMLChar.isLowSurrogate(ch2)
                            && XMLChar.isSupplemental(
                                XMLChar.supplemental(ch, ch2))) {
                            continue;
                        }
                    }
                    // Reference to invalid character which is returned                    
                    refInvalidChar = new Character(ch);
                    return false;
                }
            }
        } // end-else fDocument.isXMLVersion()

        return true;
    }