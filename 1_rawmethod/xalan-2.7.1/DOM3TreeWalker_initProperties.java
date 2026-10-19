/**
     * Initializes fFeatures based on the DOMConfiguration Parameters set.
     *
     * @param properties DOMConfiguraiton properties that were set and which are
     * to be used while serializing the DOM. 
     */
    protected void initProperties(Properties properties) {

        for (Enumeration keys = properties.keys(); keys.hasMoreElements();) {

            final String key = (String) keys.nextElement();
    
            // caonical-form
            // Other features will be enabled or disabled when this is set to true or false.

            // error-handler; set via the constructor

            // infoset
            // Other features will be enabled or disabled when this is set to true

            // A quick lookup for the given set of properties (cdata-sections ...)
            final Object iobj = s_propKeys.get(key);
            if (iobj != null) {
                if (iobj instanceof Integer) {
                    // Dealing with a property that has a simple bit value that
                    // we need to set

                    // cdata-sections			
                    // comments
                    // element-content-whitespace
                    // entities
                    // namespaces
                    // namespace-declarations
                    // split-cdata-sections
                    // well-formed
                    // discard-default-content
                    final int BITFLAG = ((Integer) iobj).intValue();
                    if ((properties.getProperty(key).endsWith("yes"))) {
                        fFeatures = fFeatures | BITFLAG;
                    } else {
                        fFeatures = fFeatures & ~BITFLAG;
                    }
                } else {
                    // We are interested in the property, but it is not
                    // a simple bit that we need to set.

                    if ((DOMConstants.S_DOM3_PROPERTIES_NS
                        + DOMConstants.DOM_FORMAT_PRETTY_PRINT)
                        .equals(key)) {
                        // format-pretty-print; set internally on the serializers via xsl:output properties in LSSerializer
                        if ((properties.getProperty(key).endsWith("yes"))) {
                            fSerializer.setIndent(true);
                            fSerializer.setIndentAmount(3);
                        } else {
                            fSerializer.setIndent(false);
                        }
                    } else if (
                        (DOMConstants.S_XSL_OUTPUT_OMIT_XML_DECL).equals(
                            key)) {
                        // omit-xml-declaration; set internally on the serializers via xsl:output properties in LSSerializer
                        if ((properties.getProperty(key).endsWith("yes"))) {
                            fSerializer.setOmitXMLDeclaration(true);
                        } else {
                            fSerializer.setOmitXMLDeclaration(false);
                        }
                    } else if (
                        (
                            DOMConstants.S_XERCES_PROPERTIES_NS
                                + DOMConstants.S_XML_VERSION).equals(
                            key)) {
                        // Retreive the value of the XML Version attribute via the xml-version
                        String version = properties.getProperty(key);
                        if ("1.1".equals(version)) {
                            fIsXMLVersion11 = true;
                            fSerializer.setVersion(version);
                        } else {
                            fSerializer.setVersion("1.0");
                        }
                    } else if (
                        (DOMConstants.S_XSL_OUTPUT_ENCODING).equals(key)) {
                        // Retreive the value of the XML Encoding attribute
                        String encoding = properties.getProperty(key);
                        if (encoding != null) {
                            fSerializer.setEncoding(encoding);
                        }
                    } else if ((DOMConstants.S_XERCES_PROPERTIES_NS
                            + DOMConstants.DOM_ENTITIES).equals(key)) {
                        // Preserve entity references in the document
                        if ((properties.getProperty(key).endsWith("yes"))) {
                            fSerializer.setDTDEntityExpansion(false);
                        }
                        else {
                            fSerializer.setDTDEntityExpansion(true);
                        }
                    } else {
                        // We shouldn't get here, ever, now what?
                    }
                }
            }
        }
        // Set the newLine character to use
        if (fNewLine != null) {
            fSerializer.setOutputProperty(OutputPropertiesFactory.S_KEY_LINE_SEPARATOR, fNewLine);
        }
    }