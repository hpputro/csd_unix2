/**
   * Tell if the namespace URI and local name match this
   * element.
   * @param uri The namespace uri, which may be null.
   * @param localName The local name of an element, which may be null.
   *
   * @return true if the uri and local name arguments are considered 
   * to match the uri and local name of this element def.
   */
  private boolean QnameEquals(String uri, String localName)
  {

    return (equalsMayBeNullOrZeroLen(m_namespace, uri)
            && (equalsMayBeNullOrZeroLen(m_name, localName)
                || equalsMayBeNullOrZeroLen(m_nameAlias, localName)));
  }