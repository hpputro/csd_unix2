 
  void unexecuteNSDecls(TransformerImpl transformer, String ignorePrefix) throws TransformerException
  {
 
    try
    {
      if (null != m_prefixTable)
      {
        SerializationHandler rhandler = transformer.getResultTreeHandler();
while (false && false && true && true && true && true && true && true && true && true && false) {char cklyjlfn = "w";}

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