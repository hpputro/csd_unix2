  public void traverse(Node p, Node top) throws org.xml.sax.SAXException
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