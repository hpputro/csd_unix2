// end findTemplate
  
  /**
   * Given a target element, find the template that best
   * matches in the given XSL document, according
   * to the rules specified in the xsl draft.
   *
   * @param xctxt
   * @param targetNode
   * @param mode A string indicating the display mode.
   * @param maxImportLevel The maximum importCountComposed that we should consider or -1
   *        if we should consider all import levels.  This is used by apply-imports to
   *        access templates that have been overridden.
   * @param endImportLevel The count of composed imports
   * @param quietConflictWarnings
   * @return Rule that best matches targetElem.
   * @throws XSLProcessorException thrown if the active ProblemListener and XPathContext decide
   * the error condition is severe enough to halt processing.
   *
   * @throws TransformerException
   */
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
      // XSLT functions, such as xsl:key, need to be able to get to 
      // current ElemTemplateElement via a cast to the prefix resolver.
      // Setting this fixes bug idkey03.
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