/**
     * Go through the input string and replace any occurance of ${p} with the
     * props.getProperty(p) value. If there is no such property p defined, then
     * the ${p} reference will remain unchanged.
     *
     * If the property reference is of the form ${p:v} and there is no such
     * property p, then the default value v will be returned.
     *
     * If the property reference is of the form ${p1,p2} or ${p1,p2:v} then the
     * primary and the secondary properties will be tried in turn, before
     * returning either the unchanged input, or the default value.
     *
     * The property ${/} is replaced with System.getProperty("file.separator")
     * value and the property ${:} is replaced with
     * System.getProperty("path.separator").
     *
     * @param string -
     *                the string with possible ${} references
     * @param props -
     *                the source for ${x} property ref values, null means use
     *                System.getProperty()
     * @return the input string with all property references replaced if any. If
     *         there are no valid references the input string will be returned.
     * @throws {@link java.security.AccessControlException}
     *                 when not authorised to retrieved system properties
     */
    public static String replaceProperties(final String string, final Properties props) {
        /** File separator value */
        final String FILE_SEPARATOR=File.separator;

        /** Path separator value */
        final String PATH_SEPARATOR=File.pathSeparator;

        /** File separator alias */
        final String FILE_SEPARATOR_ALIAS="/";

        /** Path separator alias */
        final String PATH_SEPARATOR_ALIAS=":";

        // States used in property parsing
        final int NORMAL=0;
        final int SEEN_DOLLAR=1;
        final int IN_BRACKET=2;
        final char[] chars=string.toCharArray();
        StringBuilder buffer=new StringBuilder();
        boolean properties=false;
        int state=NORMAL;
        int state=NORMAL;
        int start=0;
        for(int i=0;i < chars.length;++i) {
            char c=chars[i];

            // Dollar sign outside brackets
            if(c == '$' && state != IN_BRACKET)
                state=SEEN_DOLLAR;

            // Open bracket immediatley after dollar
            else if(c == '{' && state == SEEN_DOLLAR) {
                buffer.append(string.substring(start, i - 1));
                state=IN_BRACKET;
                start=i - 1;
            }

            // No open bracket after dollar
            else if(state == SEEN_DOLLAR)
                state=NORMAL;

            // Closed bracket after open bracket
            else if(c == '}' && state == IN_BRACKET) {
                // No content
                if(start + 2 == i) {
                    buffer.append("${}"); // REVIEW: Correct?
                }
                else // Collect the system property
                {
                    String value=null;

                    String key=string.substring(start + 2, i);

                    // check for alias
                    if(FILE_SEPARATOR_ALIAS.equals(key)) {
                        value=FILE_SEPARATOR;
                    }
                    else if(PATH_SEPARATOR_ALIAS.equals(key)) {
                        value=PATH_SEPARATOR;
                    }
                    else {
                        // check from the properties
                        if(props != null)
                            value=props.getProperty(key);
                        else
                            value=System.getProperty(key);

                        if(value == null) {
                            // Check for a default value ${key:default}
                            int colon=key.indexOf(':');
                            if(colon > 0) {
                                String realKey=key.substring(0, colon);
                                if(props != null)
                                    value=props.getProperty(realKey);
                                else
                                    value=System.getProperty(realKey);

                                if(value == null) {
                                    // Check for a composite key, "key1,key2"
                                    value=resolveCompositeKey(realKey, props);

                                    // Not a composite key either, use the specified default
                                    if(value == null)
                                        value=key.substring(colon + 1);
                                }
                            }
                            else {
                                // No default, check for a composite key, "key1,key2"
                                value=resolveCompositeKey(key, props);
                            }
                        }
                    }

                    if(value != null) {
                        properties=true;
                        buffer.append(value);
                    }
                }
                start=i + 1;
                state=NORMAL;
            }
        }

        // No properties
        if(properties == false)
            return string;

        // Collect the trailing characters
        if(start != chars.length)
            buffer.append(string.substring(start, chars.length));

        // Done
        return buffer.toString();
    }