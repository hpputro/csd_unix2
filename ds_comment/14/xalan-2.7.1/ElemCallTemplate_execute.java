/**
   * Invoke a named template.
   * @see <a href="http://www.w3.org/TR/xslt#named-templates">named-templates in XSLT Specification</a>
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(
          TransformerImpl transformer)
            throws TransformerException
  {

    if (transformer.getDebug())
      transformer.getTraceManager().fireTraceEvent(this);

    if (null != m_template)
    {
      XPathContext xctxt = transformer.getXPathContext();
      VariableStack vars = xctxt.getVarStack();

      int thisframe = vars.getStackFrame();
      int nextFrame = vars.link(m_template.m_frameSize);
      
      // We have to clear the section of the stack frame that has params 
      // so that the default param evaluation will work correctly.
      if(m_template.m_inArgsSize > 0)
      {
        vars.clearLocalSlots(0, m_template.m_inArgsSize);
      
        if(null != m_paramElems)
        {
          int currentNode = xctxt.getCurrentNode();
          int currentNode = xctxt.getCurrentNode();
          vars.setStackFrame(thisframe);
          int size = m_paramElems.length;
          
          for (int i = 0; i < size; i++) 
          {
            ElemWithParam ewp = m_paramElems[i];
            if(ewp.m_index >= 0)
            {
              if (transformer.getDebug())
                transformer.getTraceManager().fireTraceEvent(ewp);
              XObject obj = ewp.getValue(transformer, currentNode);
              if (transformer.getDebug())
                transformer.getTraceManager().fireTraceEndEvent(ewp);
              
              // Note here that the index for ElemWithParam must have been 
              // statically made relative to the xsl:template being called, 
              // NOT this xsl:template.
              vars.setLocalVariable(ewp.m_index, obj, nextFrame);
            }
          }
          vars.setStackFrame(nextFrame);
        }
      }
      
      SourceLocator savedLocator = xctxt.getSAXLocator();

      try
      {
        xctxt.setSAXLocator(m_template);

        // template.executeChildTemplates(transformer, sourceNode, mode, true);
        transformer.pushElemTemplateElement(m_template);
        m_template.execute(transformer);
      }
      finally
      {
        transformer.popElemTemplateElement();
        xctxt.setSAXLocator(savedLocator);
        // When we entered this function, the current 
        // frame buffer (cfb) index in the variable stack may 
        // have been manually set.  If we just call 
        // unlink(), however, it will restore the cfb to the 
        // previous link index from the link stack, rather than 
        // the manually set cfb.  So, 
        // the only safe solution is to restore it back 
        // to the same position it was on entry, since we're 
        // really not working in a stack context here. (Bug4218)
        vars.unlink(thisframe);
      }
    }
    else
    {
      transformer.getMsgMgr().error(this, XSLTErrorResources.ER_TEMPLATE_NOT_FOUND,
                                    new Object[]{ m_templateName });  //"Could not find template named: '"+templateName+"'");
    }
    
    if (transformer.getDebug())
	  transformer.getTraceManager().fireTraceEndEvent(this); 

  }