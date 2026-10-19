/**
   * Output an object to the result tree by doing the right conversions.
   * This is public for access by extensions.
   *
   *
   * @param stylesheetTree The owning stylesheet
   * @param obj the Java object to output. If its of an X<something> type
   *        then that conversion is done first and then sent out.
   *
   * @throws TransformerException
   * @throws java.io.FileNotFoundException
   * @throws java.io.IOException
   * @throws java.net.MalformedURLException
   */
  public void outputToResultTree(Stylesheet stylesheetTree, Object obj)
          throws TransformerException, java.net.MalformedURLException,
                 java.io.FileNotFoundException, java.io.IOException
  {

    try
    {
      SerializationHandler rtreeHandler = transformer.getResultTreeHandler();
      XPathContext xctxt = transformer.getXPathContext();
      XObject value;

      // Make the return object into an XObject because it
      // will be easier below.  One of the reasons to do this
      // is to keep all the conversion functionality in the
      // XObject classes.
      if (obj instanceof XObject)
      {
        value = (XObject) obj;
      }
      else if (obj instanceof String)
      {
        value = new XString((String) obj);
      }
      else if (obj instanceof Boolean)
      {
        value = new XBoolean(((Boolean) obj).booleanValue());
      }
      else if (obj instanceof Double)
      {
        value = new XNumber(((Double) obj).doubleValue());
      }
      else if (obj instanceof DocumentFragment)
      {
        int handle = xctxt.getDTMHandleFromNode((DocumentFragment)obj);
        
        value = new XRTreeFrag(handle, xctxt);
      }
      else if (obj instanceof DTM)
      {
        DTM dtm = (DTM)obj;
        DTMIterator iterator = new DescendantIterator();
        // %%ISSUE%% getDocument may not be valid for DTMs shared by multiple
        // document trees, eg RTFs. But in that case, we shouldn't be trying
        // to iterate over the whole DTM; we should be iterating over 
        // dtm.getDocumentRoot(rootNodeHandle), and folks should have told us
        // this by passing a more appropriate type.
        iterator.setRoot(dtm.getDocument(), xctxt);
        value = new XNodeSet(iterator);
      }
      else if (obj instanceof DTMAxisIterator)
      {
        DTMAxisIterator iter = (DTMAxisIterator)obj;
        DTMIterator iterator = new OneStepIterator(iter, -1);
        value = new XNodeSet(iterator);
      }
      else if (obj instanceof DTMIterator)
      {
        value = new XNodeSet((DTMIterator) obj);
      }
      else if (obj instanceof NodeIterator)
      {
        value = new XNodeSet(new org.apache.xpath.NodeSetDTM(((NodeIterator)obj), xctxt));
      }
      else if (obj instanceof org.w3c.dom.Node)
      {
        value =
          new XNodeSet(xctxt.getDTMHandleFromNode((org.w3c.dom.Node) obj),
                       xctxt.getDTMManager());
      }
      else
      {
        value = new XString(obj.toString());
      }

      int type = value.getType();
      String s;

      switch (type)
      {
      case XObject.CLASS_BOOLEAN :
      case XObject.CLASS_NUMBER :
      case XObject.CLASS_STRING :
        s = value.str();

        rtreeHandler.characters(s.toCharArray(), 0, s.length());
        break;

      case XObject.CLASS_NODESET :  // System.out.println(value);
        DTMIterator nl = value.iter();
        
        int pos;

        while (DTM.NULL != (pos = nl.nextNode()))
        {
          DTM dtm = nl.getDTM(pos);
          int top = pos;

          while (DTM.NULL != pos)
          {
            rtreeHandler.flushPending();
            ClonerToResultTree.cloneToResultTree(pos, dtm.getNodeType(pos), 
                                                   dtm, rtreeHandler, true);

            int nextNode = dtm.getFirstChild(pos);

            while (DTM.NULL == nextNode)
            {
              if (DTM.ELEMENT_NODE == dtm.getNodeType(pos))
              {
                rtreeHandler.endElement("", "", dtm.getNodeName(pos));
              }

              if (top == pos)
                break;

              nextNode = dtm.getNextSibling(pos);

              if (DTM.NULL == nextNode)
              {
                pos = dtm.getParent(pos);

                if (top == pos)
                {
                  if (DTM.ELEMENT_NODE == dtm.getNodeType(pos))
                  {
                    rtreeHandler.endElement("", "", dtm.getNodeName(pos));
                  }

                  nextNode = DTM.NULL;

                  break;
                }
              }
            }

            pos = nextNode;
          }
        }
        break;
      case XObject.CLASS_RTREEFRAG :
        SerializerUtils.outputResultTreeFragment(
            rtreeHandler, value, transformer.getXPathContext());
//        rtreeHandler.outputResultTreeFragment(value,
//                                              transformer.getXPathContext());
        break;
      }
    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }