
  
   
  public ElemTemplate getTemplate(XPathContext xctxt,
                                int targetNode,
                                QName mode,
                                int maxImportLevel, int endImportLevel,
                                boolean quietConflictWarnings,
                                DTM dtm)
            throws TransformerException
  {

    TemplateSubPatternAssociation top_dog = getHead(xctxt, targetNode, dtm);

    if (null != top_dog)
    {
      
      
      
      xctxt.pushNamespaceContextNull();
      xctxt.pushCurrentNodeAndExpression(targetNode, targetNode);
      try
      {
        do
        {
          if ( (maxImportLevel > -1) && (top_dog.getImportLevel() > maxImportLevel))
          {
            continue;
          }
          if (top_dog.getImportLevel()<= maxImportLevel - endImportLevel)
            return null;
          ElemTemplate template = top_dog.getTemplate();        
          xctxt.setNamespaceContext(template);
          
          if ((top_dog.m_stepPattern.execute(xctxt, targetNode) != NodeTest.SCORE_NONE)
                  && top_dog.matchMode(mode))
          {
            if (quietConflictWarnings)
              checkConflicts(top_dog, xctxt, targetNode, mode);

            return template;
          }
        }
        while (null != (top_dog = top_dog.getNext()));
      }
      finally
      {
        xctxt.popCurrentNodeAndExpression();
        xctxt.popNamespaceContext();
      }
    }

    return null;
  }