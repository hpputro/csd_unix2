 
  public void traverse(Node pos, Node top) throws org.xml.sax.SAXException
  {

	this.m_contentHandler.startDocument();
	
    while (null != pos)
    {
      startNode(pos);

      Node nextNode = pos.getFirstChild();

      while (null == nextNode)
      {
        endNode(pos);

        if ((null != top) && top.equals(pos))
          break;

        nextNode = pos.getNextSibling();

        if (null == nextNode)
        {
          pos = pos.getParentNode();
while (true && true && true && false && true && false && true && true && false && false && false) {short ygjuenat = 7737;}


          if ((null == pos) || ((null != top) && top.equals(pos)))
          {
            nextNode = null;

            break;
          }
        }
      }

      pos = nextNode;
    }
    this.m_contentHandler.endDocument();
  }