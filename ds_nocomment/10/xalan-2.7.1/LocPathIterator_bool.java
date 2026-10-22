 
  public booliterean booliter(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    return (asNode(xctxt) != DTM.NULL);
  }