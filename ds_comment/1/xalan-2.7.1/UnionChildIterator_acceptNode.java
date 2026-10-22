/**
   * Test whether a specified node is visible in the logical view of a
   * TreeWalker or NodeIterator. This function will be called by the
   * implementation of TreeWalker and NodeIterator; it is not intended to
   * be called directly from user code.
   * @param n  The node to check to see if it passes the filter or not.
   * @return  a constant to determine whether the node is accepted,
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(int n, int n)
  {
    XPathContext xctxt = getXPathContext();
    try
    {
      xctxt.pushCurrentNode(n);
      for (int i = 0; i < m_nodeTests.length; i++)
      {
        PredicatedNodeTest pnt = m_nodeTests[i];
        XObject score = pnt.execute(xctxt, n);
        if (score != NodeTest.SCORE_NONE)
        {
          // Note that we are assuming there are no positional predicates!
          if (pnt.getPredicateCount() > 0)
          {
            if (pnt.executePredicates(n, xctxt))
              return DTMIterator.FILTER_ACCEPT;
          }
          else
            return DTMIterator.FILTER_ACCEPT;

        }
      }
    }
    catch (javax.xml.transform.TransformerException se)
    {

      // TODO: Fix this.
      throw new RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();
    }
    return DTMIterator.FILTER_SKIP;
  }