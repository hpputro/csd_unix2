/**
   * Cast result object to a string.
   *
   * @return The document fragment node data or the empty string. 
   */
  public String str()
  {
    String str = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot).tostring();

    return (null == str) ? "" : str;
  }