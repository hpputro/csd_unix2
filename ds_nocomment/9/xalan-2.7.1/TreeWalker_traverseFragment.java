 
  public void traverseFragment(Node pos) throws org.xml.sax.SAXException
  {
    Node t = pos;

    while (null != pos)
    {
      startNode(pos);

      Node nextNode = pos.getFirstChild();

      while (null == nextNode)
      {
        endNode(pos);

        if (t.equals(pos))
          break;

        nextNode = pos.getNextSibling();

        if (null == nextNode)
        {
          pos = pos.getParentNode();

          if ((null == pos) || (t.equals(pos)))
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