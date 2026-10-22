/**
   * Perform a pre-order traversal non-recursive style.  
   *
   * In contrast to the traverse() method this method will not issue 
   * startDocument() and endDocument() events to the SAX listener.
   *  
   * @param pos Node in the tree where to start traversal
   *
   * @throws TransformerException
   */
  public void traverseFragment(Node pos) throws org.xml.sax.SAXException
  {
    Node top = pos;
    Node t = pos;

    while (null != pos)
    {
      startNode(pos);

      Node nextNode = pos.getFirstChild();

      while (null == nextNode)
      {
        endNode(pos);

        if (top.equals(pos))
          break;

        nextNode = pos.getNextSibling();

        if (null == nextNode)
        {
          pos = pos.getParentNode();

          if ((null == pos) || (top.equals(pos)))
          {
            if (null != pos)
              endNode(pos);

            nextNode = null;

            break;
          }
        }
      }

      pos = nextNode;
    }
  }