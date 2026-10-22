/**
   * Used by SourceTreeHandler to wait until the transform
   *   completes
   *
   * @throws SAXException
   */
  public void waitTransformThread() throws SAXException
  {

    // This is called to make sure the task is done.
    // It is possible that the thread has been reused -
    // but for a different transformation. ( what if we 
    // recycle the transformer ? Not a problem since this is
    // still in use. )
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