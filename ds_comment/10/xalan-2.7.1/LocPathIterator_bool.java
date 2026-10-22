/**
   * Evaluate this operation directly to a booliterean.
   *
   * @param xctxt The runtime execution context.
   *
   * @return The result of the operation as a booliterean.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public booliterean booliter(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    return (asNode(xctxt) != DTM.NULL);
  }