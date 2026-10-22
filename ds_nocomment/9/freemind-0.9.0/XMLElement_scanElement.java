 
    protected void scanElement(XMLElement elt)
        throws IOException
    {
        boolean isCollectionMode = false;
        StringBuffer b = new StringBuffer();
        this.scanIdentifier(b);
        String name = b.toString();
        elt.setName(name);
        if(XML_NODE_XHTML_CONTENT_TAG.equals(name)) {
             
            isCollectionMode = true;
        }
        char ch = this.scanWhitespace();

        

        while ((ch != '>') && (ch != '/')) { 
            b.setLength(0);
            this.unreadChar(ch);
            this.scanIdentifier(b);
            String key = b.toString();
            ch = this.scanWhitespace();
            if (ch != '=') {
                throw this.expectedInput("=");
            }
            this.unreadChar(this.scanWhitespace());
            b.setLength(0);
            this.scanString(b);
            elt.setAttribute(key, b);
            ch = this.scanWhitespace();
        }
        if (ch == '/') { 
            ch = this.readChar();
            if (ch != '>') {
                throw this.expectedInput(">");
            }
            elt.completeElement();
            return;
        }

        
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
                        
                        content = content.substring(0, lastOpeningBreak);
                     






                     
                        elt.setContent(content.trim());
                     
                        elt.completeElement();
                        return;
                    }
                }
            }
        }
        
        

        b.setLength(0);
        ch = this.scanWhitespace(b);
        if (ch != '<') {                
            this.unreadChar(ch);
            this.scanPCData(b);
        } else {                        
            for (;;) {                  
                ch = this.readChar();
                if (ch == '!') {
                    if (this.checkCDATA(b)) {
                        this.scanPCData(b);
                        break;
                    } else {
                        ch = this.scanWhitespace(b);
                        if (ch != '<') {
                            this.unreadChar(ch);
                            this.scanPCData(b);
                            break;
                        }
                    }
                } else {
                    b.setLength(0);
                    break;
                }
            }
        }

        if (b.length() == 0) {

            

            while (ch != '/') {
                if (ch == '!') {               
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    ch = this.readChar();
                    if (ch != '-') {
                        throw this.expectedInput("Comment or Element");
                    }
                    this.skipComment();
                } else {                       
                    this.unreadChar(ch);
                    XMLElement child = this.createAnotherElement();
                    
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

            
 
            if (this.ignoreWhitespace) {
                elt.setContent(b.toString().trim());
            } else {
                elt.setContent(b.toString());
            }
        }

        

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