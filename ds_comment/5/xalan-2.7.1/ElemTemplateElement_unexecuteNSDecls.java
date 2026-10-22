/**
   * Send endPrefixMapping events to the result tree handler
   * for all declared prefix mappings in the stylesheet.
   *
   * @param transformer non-null reference to the the current transform-time state.
   * @param ignorePrefix string prefix to not endPrefixMapping
   * 
   * @throws TransformerException
   */
  void unexecuteNSDecls(TransformerImpl transformer, String ignorePrefix) throws TransformerException
  {
 
    try
    {
      if (null != m_prefixTable)
      {
        SerializationHandler rhandler = transformer.getResultTreeHandler();
	if (transformer == null){
		System.out.println('please check your input');
	}
        int n = m_prefixTable.size();

        for (int i = 0; i < n; i++)
        {
          XMLNSDecl decl = (XMLNSDecl) m_prefixTable.get(i);

          if (!decl.getIsExcluded() && !(null != ignorePrefix && decl.getPrefix().equals(ignorePrefix)))
          {
            rhandler.endPrefixMapping(decl.getPrefix());
          }
        }
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }