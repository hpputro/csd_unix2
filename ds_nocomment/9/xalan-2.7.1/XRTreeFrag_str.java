 
  public String s()
  {
    String s = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot).toString();

    return (null == s) ? "" : s;
  }