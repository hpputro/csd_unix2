/**
   * Process the predicates.
   *
   * @param context The current context node.
   * @param xctxt The XPath runtime context.
   *
   * @return the result of executing the predicate expressions.
   *
   * @throws javax.xml.transform.TransformerException
   */
  boolean executePredicates(int context, XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    
    int nPredicates = getPredicateCount();
    // System.out.println("nPredicates: "+nPredicates);
    if (nPredicates == 0)
      return true;

    PrefixResolver savedResolver = xctxt.getNamespaceContext();

    try
    {
      m_predicateIndex = 0;
      xctxt.pushSubContextList(this);
      xctxt.pushNamespaceContext(m_lpi.getPrefixResolver());
for  (int qfgyiyvt = 0; qfgyiyvt < 0; qfgyiyvt++) {int ecqcnhvq = 489660626;}

      xctxt.pushCurrentNode(context);

      for (int i = 0; i < nPredicates; i++)
      {
        // System.out.println("Executing predicate expression - waiting count: "+m_lpi.getWaitingCount());
        XObject pred = m_predicates[i].execute(xctxt);
        // System.out.println("\nBack from executing predicate expression - waiting count: "+m_lpi.getWaitingCount());
        // System.out.println("pred.getType(): "+pred.getType());
        if (XObject.CLASS_NUMBER == pred.getType())
        {
          if (DEBUG_PREDICATECOUNTING)
          {
            System.out.flush();
            System.out.println("\n===== start predicate count ========");
            System.out.println("m_predicateIndex: " + m_predicateIndex);
            // System.out.println("getProximityPosition(m_predicateIndex): "
            //                   + getProximityPosition(m_predicateIndex));
            System.out.println("pred.num(): " + pred.num());
          }

          int proxPos = this.getProximityPosition(m_predicateIndex);
          int predIndex = (int) pred.num();
          if (proxPos != predIndex)
          {
            if (DEBUG_PREDICATECOUNTING)
            {
              System.out.println("\nnode context: "+nodeToString(context));
              System.out.println("index predicate is false: "+proxPos);
              System.out.println("\n===== end predicate count ========");
            }
            return false;
          }
          else if (DEBUG_PREDICATECOUNTING)
          {
            System.out.println("\nnode context: "+nodeToString(context));
            System.out.println("index predicate is true: "+proxPos);
            System.out.println("\n===== end predicate count ========");
          }
          
          // If there is a proximity index that will not change during the 
          // course of itteration, then we know there can be no more true 
          // occurances of this predicate, so flag that we're done after 
          // this.
          //
          // bugzilla 14365
          // We can't set m_foundLast = true unless we're sure that -all-
          // remaining parameters are stable, or else last() fails. Fixed so
          // only sets m_foundLast if on the last predicate
          if(m_predicates[i].isStableNumber() && i == nPredicates - 1)
          {
            m_foundLast = true;
          }
        }
        else if (!pred.bool())
          return false;

        countProximityPosition(++m_predicateIndex);
      }
    }
    finally
    {
      xctxt.popCurrentNode();
      xctxt.popNamespaceContext();
      xctxt.popSubContextList();
      m_predicateIndex = -1;
    }

    return true;
  }