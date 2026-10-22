/**
   * Figure out whether node2 should be considered as being later
   * in the document than node1, in Document Order as defined
   * by the XPath model. This may not agree with the ordering defined
   * by other XML applications.
   * <p>
   * There are some cases where ordering isn't defined, and neither are
   * the results of this function -- though we'll generally return true.
   * 
   * TODO: Make sure this does the right thing with attribute nodes!!!
   *
   * @param node1 DOM Node to perform position comparison on.
   * @param node2 DOM Node to perform position comparison on .
   * 
   * @return false if node2 comes before node1, otherwise return true.
   * You can think of this as 
   * <code>(node1.documentOrderPosition &lt;= node2.documentOrderPosition)</code>.
   */
  public static boolean isNodeAfter(Node node1, Node node2)
  {
    if (node1 == node2 || isNodeTheSame(node1, node2))
      return true;

        // Default return value, if there is no defined ordering
    boolean isNodeAfter = true;
        
    Node parent1 = getParentOfNode(node1);
    Node parent2 = getParentOfNode(node2);          

    // Optimize for most common case
    if (parent1 == parent2 || isNodeTheSame(parent1, parent2))  // then we know they are siblings
    {
      if (null != parent1)
        isNodeAfter = isNodeAfterSibling(parent1, node1, node2);
      else
      {
                  // If both parents are null, ordering is not defined.
                  // We're returning a value in lieu of throwing an exception.
                  // Not a case we expect to arise in XPath, but beware if you
                  // try to reuse this method.
                  
                  // We can just fall through in this case, which allows us
                  // to hit the debugging code at the end of the function.
          //return isNodeAfter;
      }
    }
    else
    {

      // General strategy: Figure out the lengths of the two 
      // ancestor chains, reconcile the lengths, and look for
          // the lowest common ancestor. If that ancestor is one of
          // the nodes being compared, it comes before the other.
      // Otherwise perform a sibling compare. 
                //
                // NOTE: If no common ancestor is found, ordering is undefined
                // and we return the default value of isNodeAfter.
                
      // Count parents in each ancestor chain
      int nParents1 = 2, nParents2 = 2;  // include node & parent obtained above

      while (parent1 != null)
      {
        nParents1++;

        parent1 = getParentOfNode(parent1);
      }

      while (parent2 != null)
      {
        nParents2++;

        parent2 = getParentOfNode(parent2);
      }

          // Initially assume scan for common ancestor starts with
          // the input nodes.
      Node startNode1 = node1, startNode2 = node2;

      // If one ancestor chain is longer, adjust its start point
          // so we're comparing at the same depths
      if (nParents1 < nParents2)
      {
        // Adjust startNode2 to depth of startNode1
        int adjust = nParents2 - nParents1;

        for (int i = 0; i < adjust; i++)
        {
          startNode2 = getParentOfNode(startNode2);
        }
      }
      else if (nParents1 > nParents2)
      {
        // adjust startNode1 to depth of startNode2
        int adjust = nParents1 - nParents2;

        for (int i = 0; i < adjust; i++)
        {
          startNode1 = getParentOfNode(startNode1);
        }
      }

      Node prevChild1 = null, prevChild2 = null;  // so we can "back up"

      // Loop up the ancestor chain looking for common parent
      while (null != startNode1)
      {
        if (startNode1 == startNode2 || isNodeTheSame(startNode1, startNode2))  // common parent?
        {
          if (null == prevChild1)  // first time in loop?
          {

            // Edge condition: one is the ancestor of the other.
            isNodeAfter = (nParents1 < nParents2) ? true : false;

            break;  // from while loop
          }
          else 
          {
                        // Compare ancestors below lowest-common as siblings
            isNodeAfter = isNodeAfterSibling(startNode1, prevChild1,
                                             prevChild2);

            break;  // from while loop
          }
        }  // end if(startNode1 == startNode2)

                // Move up one level and try again
        prevChild1 = startNode1;
        startNode1 = getParentOfNode(startNode1);
        prevChild2 = startNode2;
        startNode2 = getParentOfNode(startNode2);
      }  // end while(parents exist to examine)
    }  // end big else (not immediate siblings)
        
        // WARNING: The following diagnostic won't report the early
        // "same node" case. Fix if/when needed.
        
    /* -- please do not remove... very useful for diagnostics --
    System.out.println("node1 = "+node1.getNodeName()+"("+node1.getNodeType()+")"+
    ", node2 = "+node2.getNodeName()
    +"("+node2.getNodeType()+")"+
    ", isNodeAfter = "+isNodeAfter); */
    return isNodeAfter;
  }