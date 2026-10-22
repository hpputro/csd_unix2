/**
   * Get an instance of a DTM, loaded with the content from the
   * specified source.  If the unique flag is true, a new instance will
   * always be returned.  Otherwise it is up to the DTMManager to return a
   * new instance or an instance that it already created and may be being used
   * by someone else.
   * 
   * A bit of magic in this implementation: If the source is null, unique is true,
   * and incremental and doIndexing are both false, we return an instance of
   * SAX2RTFDTM, which see.
   * 
   * (I think more parameters will need to be added for error handling, and entity
   * resolution, and more explicit control of the RTF situation).
   *
   * @param source the specification of the source object.
   * @param unique true if the returned DTM must be unique, probably because it
   * is going to be mutated.
   * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
   *                         be null.
   * @param incremental true if the DTM should be built incrementally, if
   *                    possible.
   * @param doIndexing true if the caller considers it worth it to use
   *                   indexing schemes.
   *
   * @return a non-null DTM reference.
   */
  synchronized public DTM getDTM(Source source, boolean unique,
                                 DTMWSFilter whiteSpaceFilter,
                                 boolean incremental, boolean doIndexing)
  {

    if(DEBUG && null != source)
      System.out.println("Starting "+
                         (unique ? "UNIQUE" : "shared")+
                         " source: "+source.getSystemId()
                         );

    XMLStringFactory xstringFactory = m_xsf;
    int dtmPos = getFirstFreeDTMID();
    int documentID = dtmPos << IDENT_DTM_NODE_BITS;

    if ((null != source) && source instanceof DOMSource)
    {
      DOM2DTM dtm = new DOM2DTM(this, (DOMSource) source, documentID,
                                whiteSpaceFilter, xstringFactory, doIndexing);

      addDTM(dtm, dtmPos, 0);

      //      if (DUMPTREE)
      //      {
      //        dtm.dumpDTM();
      //      }

      return dtm;
    }
    else
    {
      boolean isSAXSource = (null != source)
        ? (source instanceof SAXSource) : true;
      boolean isStreamSource = (null != source)
        ? (source instanceof StreamSource) : false;

      if (isSAXSource || isStreamSource) {
        XMLReader reader = null;
        SAX2DTM dtm;

        try {
          InputSource xmlSource;

          if (null == source) {
            xmlSource = null;
          } else {
            reader = getXMLReader(source);
            xmlSource = SAXSource.sourceToInputSource(source);

            String urlOfSource = xmlSource.getSystemId();

            if (null != urlOfSource) {
              try {
                urlOfSource = SystemIDResolver.getAbsoluteURI(urlOfSource);
              } catch (Exception e) {
                // %REVIEW% Is there a better way to send a warning?
                System.err.println("Can not absolutize URL: " + urlOfSource);
              }

              xmlSource.setSystemId(urlOfSource);
            }
          }

          if (source==null && unique && !incremental && !doIndexing) {
            // Special case to support RTF construction into shared DTM.
            // It should actually still work for other uses,
            // but may be slightly deoptimized relative to the base
            // to allow it to deal with carrying multiple documents.
            //
            // %REVIEW% This is a sloppy way to request this mode;
            // we need to consider architectural improvements.
            dtm = new SAX2RTFDTM(this, source, documentID, whiteSpaceFilter,
                                 xstringFactory, doIndexing);
          }
          /**************************************************************
          // EXPERIMENTAL 3/22/02
          else if(JKESS_XNI_EXPERIMENT && m_incremental) {        	
            dtm = new XNI2DTM(this, source, documentID, whiteSpaceFilter,
                              xstringFactory, doIndexing);
          }
          **************************************************************/
          // Create the basic SAX2DTM.
          else {
            dtm = new SAX2DTM(this, source, documentID, whiteSpaceFilter,
                              xstringFactory, doIndexing);
          }

          // Go ahead and add the DTM to the lookup table.  This needs to be
          // done before any parsing occurs. Note offset 0, since we've just
          // created a new DTM.
          addDTM(dtm, dtmPos, 0);


          boolean haveXercesParser =
                     (null != reader)
                     && (reader.getClass()
                               .getName()
                               .equals("org.apache.xerces.parsers.SAXParser") );
        
          if (haveXercesParser) {
            incremental = true;  // No matter what.  %REVIEW%
          }
        
          // If the reader is null, but they still requested an incremental
          // build, then we still want to set up the IncrementalSAXSource stuff.
          if (m_incremental && incremental
               /* || ((null == reader) && incremental) */) {
            IncrementalSAXSource coParser=null;

            if (haveXercesParser) {
              // IncrementalSAXSource_Xerces to avoid threading.
              try {
                coParser =(IncrementalSAXSource)
                  Class.forName("org.apache.xml.dtm.ref.IncrementalSAXSource_Xerces").newInstance();  
              }  catch( Exception ex ) {
                ex.printStackTrace();
                coParser=null;
              }
            }

            if (coParser==null ) {
              // Create a IncrementalSAXSource to run on the secondary thread.
              if (null == reader) {
                coParser = new IncrementalSAXSource_Filter();
              } else {
                IncrementalSAXSource_Filter filter =
                         new IncrementalSAXSource_Filter();
                filter.setXMLReader(reader);
                coParser=filter;
              }
            }

			
            /**************************************************************
            // EXPERIMENTAL 3/22/02
            if (JKESS_XNI_EXPERIMENT && m_incremental &&
                  dtm instanceof XNI2DTM && 
                  coParser instanceof IncrementalSAXSource_Xerces) {
                org.apache.xerces.xni.parser.XMLPullParserConfiguration xpc=
                      ((IncrementalSAXSource_Xerces)coParser)
                                           .getXNIParserConfiguration();
              if (xpc!=null) {
                // Bypass SAX; listen to the XNI stream
                ((XNI2DTM)dtm).setIncrementalXNISource(xpc);
              } else {
                  // Listen to the SAX stream (will fail, diagnostically...)
                dtm.setIncrementalSAXSource(coParser);
              }
            } else
            ***************************************************************/
          
            // Have the DTM set itself up as IncrementalSAXSource's listener.
            dtm.setIncrementalSAXSource(coParser);

            if (null == xmlSource) {

              // Then the user will construct it themselves.
              return dtm;
            }

            if (null == reader.getErrorHandler()) {
              reader.setErrorHandler(dtm);
            }
            reader.setDTDHandler(dtm);

            try {
              // Launch parsing coroutine.  Launches a second thread,
              // if we're using IncrementalSAXSource.filter().

              coParser.startParse(xmlSource);
            } catch (RuntimeException re) {

              dtm.clearCoRoutine();

              throw re;
            } catch (Exception e) {

              dtm.clearCoRoutine();

              throw new org.apache.xml.utils.WrappedRuntimeException(e);
            }
          } else {
            if (null == reader) {

              // Then the user will construct it themselves.
              return dtm;
            }

            // not incremental
            reader.setContentHandler(dtm);
            reader.setDTDHandler(dtm);
            if (null == reader.getErrorHandler()) {
              reader.setErrorHandler(dtm);
            }

            try {
              reader.setProperty(
                               "http://xml.org/sax/properties/lexical-handler",
                               dtm);
            } catch (SAXNotRecognizedException e){}
              catch (SAXNotSupportedException e){}

            try {
              reader.parse(xmlSource);
            } catch (RuntimeException re) {
              dtm.clearCoRoutine();

              throw re;
            } catch (Exception e) {
              dtm.clearCoRoutine();

              throw new org.apache.xml.utils.WrappedRuntimeException(e);
            }
          }

          if (DUMPTREE) {
            System.out.println("Dumping SAX2DOM");
            dtm.dumpDTM(System.err);
          }

          return dtm;
        } finally {
          // Reset the ContentHandler, DTDHandler, ErrorHandler to the DefaultHandler
          // after creating the DTM.
          if (reader != null && !(m_incremental && incremental)) {
            reader.setContentHandler(m_defaultHandler);
            reader.setDTDHandler(m_defaultHandler);
            reader.setErrorHandler(m_defaultHandler);
            
            // Reset the LexicalHandler to null after creating the DTM.
            try {
              reader.setProperty("http://xml.org/sax/properties/lexical-handler", null);
            }
            catch (Exception e) {}
          }
          releaseXMLReader(reader);
        }
      } else {

        // It should have been handled by a derived class or the caller
        // made a mistake.
        throw new DTMException(XMLMessages.createXMLMessage(XMLErrorResources.ER_NOT_SUPPORTED, new Object[]{source})); //"Not supported: " + source);
      }
    }
  }