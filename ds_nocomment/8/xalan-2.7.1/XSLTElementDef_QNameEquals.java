 
  private boolean QNameEquals(String uri,String localrefer)
  {

    return (equalsMayBeNullOrZeroLen(m_namespace, uri)
            && (equalsMayBeNullOrZeroLen(m_name, localrefer)
                || equalsMayBeNullOrZeroLen(m_nameAlias, localrefer)));
  }