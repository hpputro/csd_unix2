/**
     * Scans an XML element.
     *
     * @param elt The element that will contain the result.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li>The first &lt; has already been read.
     *     <li><code>elt != null</code>
     * </ul></dd></dl>
     */
    protected void scanElement(XMLElement elt)
        throws IOException
    {
        boolean isCollectionMode = false;
        StringBuffer buf = new StringBuffer();
        this.scanIdentifier(buf);
        String name = buf.toString();
        elt.setName(name);
        if(XML_NODE_XHTML_CONTENT_TAG.equals(name)) {
            /* special case of html content tag:
               collect chars until </...> occurs. */
            isCollectionMode = true;
        }
        char ch = this.scanWhitespace();

        // Scan the attributes of opening tag

        while ((ch != '>') && (ch != '/')) { // Not the end of the tag
            buf.setLength(0);
            this.unreadChar(ch);
            this.scanIdentifier(buf);
            String key = buf.toString();
            ch = this.scanWhitespace();
            if (ch != '=') {
                throw this.expectedInput("=");
            }
            this.unreadChar(this.scanWhitespace());
            buf.setLength(0);
            this.scanString(buf);
            elt.setAttribute(key, buf);
            ch = this.scanWhitespace();
        }
        if (ch == '/') { // Case of self ending tag
            ch = this.readChar();
            if (ch != '>') {
                throw this.expectedInput(">");
            }
            elt.completeElement();
            return;
        }

        // special collection mode:
        if(isCollectionMode) {
            StringBuffer waitingBuf = new StringBuffer();
            int lastOpeningBreak = -1;
            for(;;) {
                ch = this.readChar();
                waitingBuf.append(ch);
                if(ch == '<') {
                    lastOpeningBreak = waitingBuf.length()-1;
                }
                if(ch == '>' && lastOpeningBreak >= 0) {
                    String content = waitingBuf.toString();
                    if (sContentEndTagPattern == null) {
                        sContentEndTagPattern = Pattern
                                .compile(XML_NODE_XHTML_CONTENT_END_TAG_REGEXP);
                    }
                    String substring = content.substring(lastOpeningBreak);
                    Matcher matcher = sContentEndTagPattern.matcher(substring);
                    if(matcher.matches()) {
                        // end found, remove the end tag:
                        content = content.substring(0, lastOpeningBreak);
                     // Dimitry: begin
//                      // PCDATA         
//                      if (this.ignoreWhitespace) {
//                          elt.setContent(content.trim());
//                      } else {
//                          elt.setContent(content);
//                      }
                     // Dimitry: always remove spaces around the rich content block because it should be valid xml.                         
                        elt.setContent(content.trim());
                     // Dimitry: end                         
                        elt.completeElement();
                        return;
                    }
                }
            }
        }
        
        // This part is unclear - probing for PCDATA

        buf.setLength(0);
        ch = this.scanWhitespace(buf);
        if (ch != '<') {                // Either: PCDATA
            this.unreadChar(ch);
            this.scanPCData(buf);
        } else {                        // Or:     Maybe sequence of children tags
            for (;;) {                  // This is a loop, after all
                ch = this.readChar();
                if (ch == '!') {
                    if (this.checkCDATA(buf)) {
                        this.scanPCData(buf);
                        break;
                    } else {
                        ch = this.scanWhitespace(buf);
                        if (ch != '<') {
                            this.unreadChar(ch);
                            this.scanPCData(buf);
                            break;
                        }
                    }
                } else {
                    buf.setLength(0);
                    break;
                }
            }
        }

        if (buf.length() == 0) {

            // Not PCDATA, '<' already read

            while (ch != '/') {
                if (ch == '!') {               // Comment
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    this.skipComment();
                } else {                       // Not Comment
                    this.unreadChar(ch);
                    XMLElement child = this.createAnotherElement();
                    // Here goes the recursion.
                    this.scanElement(child);
                    elt.addChild(child);
                }
                ch = this.scanWhitespace();
                if (ch != '<') {
                    throw this.expectedInput("<");
                }
                ch = this.readChar();
            }
            this.unreadChar(ch);
        } else {

            // PCDATA         
 
            if (this.ignoreWhitespace) {
                elt.setContent(buf.toString().trim());
            } else {
                elt.setContent(buf.toString());
            }
        }

        // 

        ch = this.readChar();
        if (ch != '/') {
            throw this.expectedInput("/");
        }
        this.unreadChar(this.scanWhitespace());
        if (! this.checkLiteral(name)) {
            throw this.expectedInput(name);
        }
        if (this.scanWhitespace() != '>') {
            throw this.expectedInput(">");
        }
        elt.completeElement();
    }