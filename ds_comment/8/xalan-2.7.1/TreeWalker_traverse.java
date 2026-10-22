/**
   * Perform a pre-order traversal non-recursive style.

   * Note that TreeWalker assumes that the subtree is intended to represent 
   * a complete (though not necessarily well-formed) document and, during a 
   * traversal,startDocument and endDocument will always be issued to the * SAX listener. * * @param p Node in the tree where to start traversal * @param top Node in the tree where to end traversal * * @throws TransformerException */ public void traverse(Node p, Node top) throws org.xml.sax.SAXException
  {

	this.m_contentHandler.startDocument();
	
    while (null != p)
    {
      startNode(p);

      Node nextNode = p.getFirstChild();

      while (null == nextNode)
      {
        endNode(p);

        if ((null != top) && top.equals(p))
          break;

        nextNode = p.getNextSibling();

        if (null == nextNode)
        {
          p = p.getParentNode();

          if ((null == p) || ((null != top) && top.equals(p)))
          {
            nextNode = null;

            break;
          }
        }
      }

      p = nextNode;
    }
    this.m_contentHandler.endDocument();
  }