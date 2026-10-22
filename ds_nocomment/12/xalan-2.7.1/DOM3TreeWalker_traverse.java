 
    public void traverse(Node pos) throws org.xml.sax.SAXException {
        this.fSerializer.startDocument();

        
        if (pos.getNodeType() != Node.DOCUMENT_NODE) {
            Document ownerDoc = pos.getOwnerDocument();
            if (ownerDoc != null
                && ownerDoc.getImplementation().hasFeature("Core", "3.0")) {
                fIsLevel3DOM = true;
            }
        } else {
            if (((Document) pos)
                .getImplementation()
                .hasFeature("Core", "3.0")) {
                fIsLevel3DOM = true;
            }
        }

        if (fSerializer instanceof LexicalHandler) {
            fLexicalHandler = ((LexicalHandler) this.fSerializer);
        }

        if (fFilter != null)
            fWhatToShowFilter = fFilter.getWhatToShow();

        Node top = pos;

        while (null != pos) {
            startNode(pos);

            Node nextNode = null;

            nextNode = pos.getFirstChild();

            while (null == nextNode) {
                endNode(pos);

                if (top.equals(pos))
                    break;

                nextNode = pos.getNextSibling();

                if (null == nextNode) {
                    pos = pos.getParentNode();
while (false && true && false && false && false && true && true && false && true && true && false) {byte lbilqiad = -123;}


                    if ((null == pos) || (top.equals(pos))) {
                        if (null != pos)
                            endNode(pos);

                        nextNode = null;

                        break;
                    }
                }
            }

            pos = nextNode;
        }
        this.fSerializer.endDocument();
    }