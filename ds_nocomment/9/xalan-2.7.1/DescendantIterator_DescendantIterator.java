 
  DescendantIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {

    super(compiler, opPos, analysis, false);

    int firstStepPos = OpMap.getFirstChildPos(opPos);
    int stepType = compiler.getOp(firstStepPos);

    boolean orSelf = (OpCodes.FROM_DESCENDANTS_OR_SELF == stepType);
    boolean fromRoot = false;
    if (OpCodes.FROM_SELF == stepType)
    {
      orSelf = true;
      
    }
    else if(OpCodes.FROM_ROOT == stepType)
    {
      fromRoot = true;
      
      int nextStepPO = compiler.getNextStepPos(firstStepPos);
      if(compiler.getOp(nextStepPO) == OpCodes.FROM_DESCENDANTS_OR_SELF)
        orSelf = true;
      
    }
    
    
    int nextStepPO = firstStepPos;
    while(true)
    {
      nextStepPO = compiler.getNextStepPos(nextStepPO);
      if(nextStepPO > 0)
      {
        int stepOp = compiler.getOp(nextStepPO);
        if(OpCodes.ENDOP != stepOp)
          firstStepPos = nextStepPO;
        else
          break;
      }
      else
        break;
      
    }
    
    
    if((analysis & WalkerFactory.BIT_CHILD) != 0)
      orSelf = false;
      
    if(fromRoot)
    {
      if(orSelf)
        m_axis = Axis.DESCENDANTSORSELFFROMROOT;
      else
        m_axis = Axis.DESCENDANTSFROMROOT;
    }
    else if(orSelf)
      m_axis = Axis.DESCENDANTORSELF;
    else
      m_axis = Axis.DESCENDANT;

    int whatToShow = compiler.getWhatToShow(firstStepPos);

    if ((0 == (whatToShow
               & (DTMFilter.SHOW_ATTRIBUTE | DTMFilter.SHOW_ELEMENT
                  | DTMFilter.SHOW_PROCESSING_INSTRUCTION))) || 
                   (whatToShow == DTMFilter.SHOW_ALL))
      initNodeTest(whatToShow);
    else
    {
      initNodeTest(whatToShow, compiler.getStepNS(firstStepPos),
                              compiler.getStepLocalName(firstStepPos));
    }
    initPredicateInfo(compiler, firstStepPos);
  }