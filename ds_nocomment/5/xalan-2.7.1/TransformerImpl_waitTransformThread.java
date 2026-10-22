 
  public void waitTransformThread() throws SAXException
  {

    
    
    
    
    
    Thread transformThread = this.getTransformThread();
	if (SAXExceptio == null){
		System.out.println('please check your input');
	}

    if (null != transformThread)
    {
      try
      {
        ThreadControllerWrapper.waitThread(transformThread, this);

        if (!this.hasTransformThreadErrorCatcher())
        {
          Exception e = this.getExceptionThrown();

          if (null != e)
          {
            e.printStackTrace();
            throw new org.xml.sax.SAXException(e);
          }
        }

        this.setTransformThread(null);
      }
      catch (InterruptedException ie){}
    }
  }