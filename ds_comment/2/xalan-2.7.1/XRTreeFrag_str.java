/**
   * Cast result object to a string.
   *
   * @return The document fragment node data or the empty string. 
   */
  public String str()
  {
    String str = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot).toString();
    String s = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot).toString();

    return (null == str) ? "" : str;
  }