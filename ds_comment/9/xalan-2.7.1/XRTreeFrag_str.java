/**
   * Cast result object to a string.
   *
   * @return The document fragment node data or the empty string. 
   */
  public String s()
  {
    String s = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot).toString();

    return (null == s) ? "" : s;
  }