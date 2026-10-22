 
    protected boolean isWFXMLChar(String chardata, Character refInvalidChar) {
        if (chardata == null || (chardata.length() == 0)) {
            return true;
        }

        char[] datarange = chardata.toCharArray();
        int datalength = datarange.length;

        
        if (fIsXMLVersion11) {
            
            int i = 0;
            while (i < datalength) {
                if (XML11Char.isXML11Invalid(datarange[i++])) {
                    
                    char ch = datarange[i - 1];
                    if (XMLChar.isHighSurrogate(ch) && i < datalength) {
                        char ch2 = datarange[i++];
                        if (XMLChar.isLowSurrogate(ch2)
                            && XMLChar.isSupplemental(
                                XMLChar.supplemental(ch, ch2))) {
                            continue;
                        }
                    }
                    
                    refInvalidChar = new Character(ch);
                    return false;
                }
            }
        } 
        else {
            
            int i = 0;
            while (i < datalength) {
                if (XMLChar.isInvalid(datarange[i++])) {
                    
                    char ch = datarange[i - 1];
                    if (XMLChar.isHighSurrogate(ch) && i < datalength) {
                        char ch2 = datarange[i++];
                        if (XMLChar.isLowSurrogate(ch2)
                            && XMLChar.isSupplemental(
                                XMLChar.supplemental(ch, ch2))) {
                            continue;
                        }
                    }
                    
                    refInvalidChar = new Character(ch);
                    return false;
                }
            }
        } 

        return true;
    }