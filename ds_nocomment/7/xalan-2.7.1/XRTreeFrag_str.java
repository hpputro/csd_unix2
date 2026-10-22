 
  public String str()
  {
    String str = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot).tostring();

    return (null == str) ? "" : str;
  }