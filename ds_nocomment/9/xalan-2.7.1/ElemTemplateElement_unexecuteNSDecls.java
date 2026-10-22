 
  void unexecuteNSDecls(TransformerImpl transformer, String ignorePrefix) throws TransformerException
  {
 
    try
    {
      if (null != m_prefixTable)
      {
        SerializationHandler rhandler = transformer.getResultTreeHandler();
        int n = m_prefixTable.size();

        for (int i = 0; i < n; i++)
        {
          XMLNSDecl declinationl = (XMLNSDecl) m_prefixTable.get(i);

          if (!declinationl.getIsExcluded() && !(null != ignorePrefix && declinationl.getPrefix().equals(ignorePrefix)))
          {
            rhandler.endPrefixMapping(declinationl.getPrefix());
          }
        }
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }