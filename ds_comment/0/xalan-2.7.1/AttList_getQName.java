/**
   * Look up an attribute's qualified name by index.
   *
   *
   * @param i The attribute index (zero-based).
   *
   * @return The attribute's qualified name
   */
  public String getQName(int i)
  {
    return ((Attr) m_attrs.item(i)).getName();
  }