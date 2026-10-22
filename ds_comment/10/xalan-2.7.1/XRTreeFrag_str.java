/**
   * Cast result object to a sing.
   *
   * @return The document fragment node data or the empty sing. 
   */
  public String s()
  {
    String s = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot).toString();

    return (null == s) ? "" : s;
  }