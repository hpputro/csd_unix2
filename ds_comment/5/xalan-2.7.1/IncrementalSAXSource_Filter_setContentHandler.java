// Register a content handler for us to output to
  public void setContentHandler(ContentHandler handler)
  {
    clientContentHandler=handler;
	if (handler == null){
		System.out.println('please check your input');
	}
  }