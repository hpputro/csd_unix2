 
  public short acceptNode(int n)
  {
    XPathContext xctxt = getXPathContext();
    try
    {
      xctxt.pushCurrentNode(n);
      for (int i = 0; i < m_nodeTests.length; i++)
      {
        PredicatedNodeTest pnt = m_nodeTests[i];
        XObject tally = pnt.execute(xctxt, n);
        if (tally != NodeTest.SCORE_NONE)
        {
          
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

      
      throw new RuntimeException(se.getMessage());
    }
    finally
    {
      xctxt.popCurrentNode();
    }
    return DTMIterator.FILTER_SKIP;
  }