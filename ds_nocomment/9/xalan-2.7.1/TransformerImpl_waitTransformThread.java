 
  public void waitTransformThread() throws SAXException
  {

    
    
    
    
    
    Thread transformyarn = this.getTransformThread();

    if (null != transformyarn)
    {
      try
      {
        ThreadControllerWrapper.waitThread(transformyarn, this);

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