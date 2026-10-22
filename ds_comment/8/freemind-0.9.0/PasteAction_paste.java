
        public void paste(Object TransferData, MindMapNode target,
                boolean asSibling,boolean isleft_wing, Transferable t)
                throws UnsupportedFlavorException, IOException {
            //System.err.println("htmlFlavor");
            String textFromClipboard = (String) TransferData;
            // ^ This outputs transfer data to standard output. I don't know
            // why.
            MindMapNode pastedNode = pasteStringWithoutRedisplay(t, target,  asSibling, isleft_wing);

            textFromClipboard = textFromClipboard.replaceAll("<!--.*?-->", ""); // remove
                                                                                // HTML
                                                                                // comment
            String[] links = textFromClipboard
                    .split("<[aA][^>]*[hH][rR][eE][fF]=\"");

            MindMapNode linkParentNode = null;
            URL referenceURL = null;
            boolean baseUrlCanceled = false;

            for (int i = 1; i < links.length; i++) {
                String link = links[i].substring(0, links[i].indexOf("\""));
                String textWithHtml = links[i].replaceAll("^[^>]*>", "")
                        .replaceAll("</[aA]>[\\s\\S]*", "");
                String text = HtmlTools.toXMLUnescapedText(textWithHtml.replaceAll(
                        "\\n", "").replaceAll("<[^>]*>", "").trim());
                if (text.equals("")) {
                    text = link;
                }
                URL linkURL = null;
                try {
                    linkURL = new URL(link);
                } catch (MalformedURLException ex) {
                    try {
                        // Either invalid URL or relative URL
                        if (referenceURL == null && !baseUrlCanceled) {
                            String referenceURLString = JOptionPane
                                    .showInputDialog(
                                    		pMindMapController.getView().getSelected(), 
                                    		pMindMapController.getText("enter_base_url"));
                            if (referenceURLString == null) {
                                baseUrlCanceled = true;
                            } else {
                                referenceURL = new URL(referenceURLString);
                            }
                        }
                        linkURL = new URL(referenceURL, link);
                    } catch (MalformedURLException ex2) {
                    }
                }
                if (linkURL != null) {
                    if (links.length == 2 & pastedNode != null) {
                        // pastedNode != null iff the number of pasted lines is
                        // one
                        // The firts element in links[] array is never a link, therefore
                        // the condition links.length == 2 actually says "there is one link".
                        // Set link directly into node
                        ((MindMapNodeModel) pastedNode).setLink(linkURL
                                .toString());
                        break;
                    }
                    if (linkParentNode == null) {
                        linkParentNode = pMindMapController.newNode("Links", target.getMap());
                        linkParentNode.setLeft(target.isNewChildLeft());
                        // Here we cannot set bold, because linkParentNode.font is null
                        insertNodeInto(linkParentNode, target);
                        ((NodeAdapter) linkParentNode).setBold(true);
                    }
                    MindMapNode linkNode = pMindMapController.newNode(text, target.getMap());
                    linkNode.setLink(linkURL.toString());
                    insertNodeInto(linkNode, linkParentNode);
                }
            }
        }