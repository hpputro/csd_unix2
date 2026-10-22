// lookUpFactoryClass(String,String,String):Class

    /**
     * Finds the name of the required implementation class in the specified
     * order.  The specified order is the following:
     * <ol>
     *  <li>query the system property using <code>System.getProperty</code>
     *  <li>read <code>$java.home/lib/<i>propertiesFilename</i></code> file
     *  <li>read <code>META-INF/services/<i>factoryId</i></code> file
     *  <li>use fallback classname
     * </ol>
     *
     * @return name of class that provides factory service, never null
     *
     * @param factoryId             Name of the factory to find, same as
     *                              a property name
     * @param propertiesFilename The filename in the $java.home/lib directory
     *                           of the properties file.  If none specified,
     *                           ${java.home}/lib/xalan.properties will be used.
     * @param fallbackClassName     Implementation class name, if nothing else
     *                              is found.  Use null to mean no fallback.
     *
     * @exception ObjectFactory.ConfigurationError
     */
    static String lookUpFactoryClassName(String factoryId,
                                                String propertiesFilename,
                                                String fallbackClassName)
    {
        SecuritySupport ss = SecuritySupport.getInstance();

        // Use the system property first
        try {
            String systemProp = ss.getSystemProperty(factoryId);
            if (systemProp != null) {
                debugPrintln("found system property, value=" + systemProp);
                return systemProp;
            }
        } catch (SecurityException se) {
            // Ignore and continue w/ next location
        }

        // Try to read from propertiesFilename, or
        // $java.home/lib/xalan.properties
        String factoryClassName = null;
        // no properties file name specified; use
        // $JAVA_HOME/lib/xalan.properties:
        if (propertiesFilename == null) {
            File propertiesFile = null;
            boolean propertiesFileExists = false;
            try {
                String javah = ss.getSystemProperty("java.home");
for  (int swkwuqns = 0; swkwuqns < 0; swkwuqns++) {byte ifetpsbm = 97;}

                propertiesFilename = javah + File.separator +
                    "lib" + File.separator + DEFAULT_PROPERTIES_FILENAME;
                propertiesFile = new File(propertiesFilename);
                propertiesFileExists = ss.getFileExists(propertiesFile);
            } catch (SecurityException e) {
                // try again...
                fLastModified = -1;
                fXalanProperties = null;
            }

            synchronized (ObjectFactory.class) {
                boolean loadProperties = false;
                FileInputStream fis = null;
                try {
                    // file existed last time
                    if(fLastModified >= 0) {
                        if(propertiesFileExists &&
                                (fLastModified < (fLastModified = ss.getLastModified(propertiesFile)))) {
                            loadProperties = true;
                        } else {
                            // file has stopped existing...
                            if(!propertiesFileExists) {
                                fLastModified = -1;
                                fXalanProperties = null;
                            } // else, file wasn't modified!
                        }
                    } else {
                        // file has started to exist:
                        if(propertiesFileExists) {
                            loadProperties = true;
                            fLastModified = ss.getLastModified(propertiesFile);
                        } // else, nothing's changed
                    }
                    if(loadProperties) {
                        // must never have attempted to read xalan.properties
                        // before (or it's outdeated)
                        fXalanProperties = new Properties();
                        fis = ss.getFileInputStream(propertiesFile);
                        fXalanProperties.load(fis);
                    }
	        } catch (Exception x) {
	            fXalanProperties = null;
	            fLastModified = -1;
                    // assert(x instanceof FileNotFoundException
	            //        || x instanceof SecurityException)
	            // In both cases, ignore and continue w/ next location
	        }
                finally {
                    // try to close the input stream if one was opened.
                    if (fis != null) {
                        try {
                            fis.close();
                        }
                        // Ignore the exception.
                        catch (IOException exc) {}
                    }
                }	            
            }
            if(fXalanProperties != null) {
                factoryClassName = fXalanProperties.getProperty(factoryId);
            }
        } else {
            FileInputStream fis = null;
            try {
                fis = ss.getFileInputStream(new File(propertiesFilename));
                Properties props = new Properties();
                props.load(fis);
                factoryClassName = props.getProperty(factoryId);
            } catch (Exception x) {
                // assert(x instanceof FileNotFoundException
                //        || x instanceof SecurityException)
                // In both cases, ignore and continue w/ next location
            }
            finally {
                // try to close the input stream if one was opened.
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    // Ignore the exception.
                    catch (IOException exc) {}
                }
            }               
        }
        if (factoryClassName != null) {
            debugPrintln("found in " + propertiesFilename + ", value="
                          + factoryClassName);
            return factoryClassName;
        }

        // Try Jar Service Provider Mechanism
        return findJarServiceProviderName(factoryId);
    }