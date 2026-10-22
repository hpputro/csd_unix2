/**
   * Perform a pre-order traversal non-recursive style.

   * Note that TreeWalker assumes that the subtree is intended to represent 
   * a complete (though not necessarily well-formed) document and, during a 
   * traversal, startDocument and endDocument will always be issued to the 
   * SAX listener.
   *
   * @param pos Node in the tree where to start traversal
   * @param top Node in the tree where to end traversal
   *
   * @throws TransformerException
   */
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