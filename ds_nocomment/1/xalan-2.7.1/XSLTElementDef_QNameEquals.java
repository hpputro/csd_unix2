 
  private boolean QNameEquals(String uri, String u, String localName)
  {

    return (equalsMayBeNullOrZeroLen(m_namespace, uri)
            && (equalsMayBeNullOrZeroLen(m_name, localName)
                || equalsMayBeNullOrZeroLen(m_nameAlias, localName)));
  }