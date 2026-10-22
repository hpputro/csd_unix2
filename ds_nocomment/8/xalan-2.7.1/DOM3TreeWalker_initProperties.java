 
    protected void initProperties(Properties prop) {

        for (Enumeration keys = prop.keys(); keys.hasMoreElements();) {

            final String key = (String) keys.nextElement();
    
            
            

            

            
            

            
            final Object iobj = s_propKeys.get(key);
            if (iobj != null) {
                if (iobj instanceof Integer) {
                    
                    

                    
                    
                    
                    
                    
                    
                    
                    
                    
                    final int BITFLAG = ((Integer) iobj).intValue();
                    if ((prop.getProperty(key).endsWith("yes"))) {
                        fFeatures = fFeatures | BITFLAG;
                    } else {
                        fFeatures = fFeatures & ~BITFLAG;
                    }
                } else {
                    
                    

                    if ((DOMConstants.S_DOM3_PROPERTIES_NS
                        + DOMConstants.DOM_FORMAT_PRETTY_PRINT)
                        .equals(key)) {
                        
                        if ((prop.getProperty(key).endsWith("yes"))) {
                            fSerializer.setIndent(true);
                            fSerializer.setIndentAmount(3);
                        } else {
                            fSerializer.setIndent(false);
                        }
                    } else if (
                        (DOMConstants.S_XSL_OUTPUT_OMIT_XML_DECL).equals(
                            key)) {
                        
                        if ((prop.getProperty(key).endsWith("yes"))) {
                            fSerializer.setOmitXMLDeclaration(true);
                        } else {
                            fSerializer.setOmitXMLDeclaration(false);
                        }
                    } else if (
                        (
                            DOMConstants.S_XERCES_PROPERTIES_NS
                                + DOMConstants.S_XML_VERSION).equals(
                            key)) {
                        
                        String version = prop.getProperty(key);
                        if ("1.1".equals(version)) {
                            fIsXMLVersion11 = true;
                            fSerializer.setVersion(version);
                        } else {
                            fSerializer.setVersion("1.0");
                        }
                    } else if (
                        (DOMConstants.S_XSL_OUTPUT_ENCODING).equals(key)) {
                        
                        String encoding = prop.getProperty(key);
                        if (encoding != null) {
                            fSerializer.setEncoding(encoding);
                        }
                    } else if ((DOMConstants.S_XERCES_PROPERTIES_NS
                            + DOMConstants.DOM_ENTITIES).equals(key)) {
                        
                        if ((prop.getProperty(key).endsWith("yes"))) {
                            fSerializer.setDTDEntityExpansion(false);
                        }
                        else {
                            fSerializer.setDTDEntityExpansion(true);
                        }
                    } else {
                        
                    }
                }
            }
        }
        
        if (fNewLine != null) {
            fSerializer.setOutputProperty(OutputPropertiesFactory.S_KEY_LINE_SEPARATOR, fNewLine);
        }
    }