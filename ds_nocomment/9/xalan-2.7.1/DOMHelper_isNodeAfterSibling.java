 
  private static boolean isNodeAfterSibling(Node parent, Node child1,
                                            Node child2)
  {

    boolean isNodeAfterSibling = false;
    short child1type = child1.getNodeType();
    short child2type = child2.getNodeType();

    if ((Node.ATTRIBUTE_NODE != child1type)
            && (Node.ATTRIBUTE_NODE == child2type))
    {

      
      isNodeAfterSibling = false;
    }
    else if ((Node.ATTRIBUTE_NODE == child1type)
             && (Node.ATTRIBUTE_NODE != child2type))
    {

      
      isNodeAfterSibling = true;
    }
    else if (Node.ATTRIBUTE_NODE == child1type)
    {
      NamedNodeMap children = parent.getAttributes();
      int nNodes = children.getLength();
      boolean found1 = false, found2 = false;

          
      for (int i = 0; i < nNodes; i++)
      {
        Node baby = children.item(i);

        if (child1 == baby || isNodeTheSame(child1, baby))
        {
          if (found2)
          {
            isNodeAfterSibling = false;

            break;
          }

          found1 = true;
        }
        else if (child2 == baby || isNodeTheSame(child2, baby))
        {
          if (found1)
          {
            isNodeAfterSibling = true;

            break;
          }

          found2 = true;
        }
      }
    }
    else
    {
                
                
                
                
                
                
                
                
                
                
                
      Node baby = parent.getFirstChild();
      boolean found1 = false, found2 = false;

      while (null != baby)
      {

        
        if (child1 == baby || isNodeTheSame(child1, baby))
        {
          if (found2)
          {
            isNodeAfterSibling = false;

            break;
          }

          found1 = true;
        }
        else if (child2 == baby || isNodeTheSame(child2, baby))
        {
          if (found1)
          {
            isNodeAfterSibling = true;

            break;
          }

          found2 = true;
        }

        baby = baby.getNextSibling();
      }
    }

    return isNodeAfterSibling;
  }