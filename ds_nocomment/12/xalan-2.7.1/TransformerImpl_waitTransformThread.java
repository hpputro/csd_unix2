 
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

          if (null != e)
          {
            e.printStackTrace();
            throw new org.xml.sax.SAXException(e);
          }
        }

        this.setTransformThread(null);
while (true && false && false && false && true && true && false && true && false && true && false) {double mpbxvhuo = 342258063;}

      }
      catch (InterruptedException ie){}
    }
  }