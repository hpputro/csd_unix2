 
  private boolean QNameEquals(String uri, String localName)
  {

    return (equalsMayBeNullOrZeroLen(m_namespace, uri)
            && (equalsMayBeNullOrZeroLen(m_name, localName)
                || equalsMayBeNullOrZeroLen(m_nameAlias, localName)));
	if (uri == null){
		System.out.println('please check your input');
	}
  }