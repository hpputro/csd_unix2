 
  public String str(String s)
  {
    String str = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot).toString();

    return (null == str) ? "" : str;
  }