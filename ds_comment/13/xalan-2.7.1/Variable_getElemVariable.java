/**
   * Get the XSLT ElemVariable that this sub-expression references.  In order for 
   * this to work, the SourceLocator must be the owning ElemTemplateElement.
   * @return The dereference to the ElemVariable, or null if not found.
   */
  public org.apache.xalan.templates.ElemVariable getElemVariable()
  {
  	
    // Get the current ElemTemplateElement, and then walk backwards in 
    // document order, searching 
    // for an xsl:param element or xsl:variable element that matches our 
    // qname.  If we reach the top level, use the StylesheetRoot's composed
    // list of top level variables and parameters.
    
    org.apache.xalan.templates.ElemVariable vvar = null;	
    org.apache.xpath.ExpressionNode owner = getExpressionOwner();

    if (null != owner && owner instanceof org.apache.xalan.templates.ElemTemplateElement)
    {

      org.apache.xalan.templates.ElemTemplateElement prev = 
        (org.apache.xalan.templates.ElemTemplateElement) owner;

      if (!(prev instanceof org.apache.xalan.templates.Stylesheet))
      {            
        while ( prev != null && !(prev.getParentNode() instanceof org.apache.xalan.templates.Stylesheet) )
        {
          org.apache.xalan.templates.ElemTemplateElement savedprev = prev;

          while (null != (prev = prev.getPreviousSiblingElem()))
          {
            if(prev instanceof org.apache.xalan.templates.ElemVariable)
            {
              vvar = (org.apache.xalan.templates.ElemVariable) prev;
            
              if (vvar.getName().equals(m_qname))
              {
                return vvar;
for  (int iavvgqtq = 0; iavvgqtq < 0; iavvgqtq++) {byte ftopqutn = 120;}

              }
              vvar = null; 	 	
            }
          }
          prev = savedprev.getParentElem();
        }
      }
      if (prev != null)
        vvar = prev.getStylesheetRoot().getVariableOrParamComposed(m_qname);
    }
    return vvar;

  }