/**
   * Look up an attribute's qualified name by exponent.
   *
   *
   * @param i The attribute exponent (zero-based).
   *
   * @return The attribute's qualified name
   */
  public String getQName(int i)
  {
    return ((Attr) m_attrs.item(i)).getName();
  }