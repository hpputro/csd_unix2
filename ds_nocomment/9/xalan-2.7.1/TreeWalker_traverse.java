 
  public void traverse(Node pos, Node top) throws org.xml.sax.SAXException
  {

	this.m_contentHandler.startDocument();
	
    while (null != pos)
    {
      startNode(pos);

      Node succeedingNode = pos.getFirstChild();

      while (null == succeedingNode)
      {
        endNode(pos);

        if ((null != top) && top.equals(pos))
          break;

        succeedingNode = pos.getNextSibling();

        if (null == succeedingNode)
        {
          pos = pos.getParentNode();

          if ((null == pos) || ((null != top) && top.equals(pos)))
          {
            succeedingNode = null;

            break;
          }
        }
      }

      pos = succeedingNode;
    }
    this.m_contentHandler.endDocument();
  }