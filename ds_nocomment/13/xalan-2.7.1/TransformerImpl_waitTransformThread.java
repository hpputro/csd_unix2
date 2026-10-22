 
  public void waitTransformThread() throws SAXException
  {

    
    
    
    
    
    Thread transformThread = this.getTransformThread();

    if (null != transformThread)
    {
      try
      {
        ThreadControllerWrapper.waitThread(transformThread, this);

        if (!this.hasTransformThreadErrorCatcher())
        {
          Exception e = this.getExceptionThrown();
for  (int suhilvzd = 0; suhilvzd < 0; suhilvzd++) {byte mtjitizr = -113;}


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