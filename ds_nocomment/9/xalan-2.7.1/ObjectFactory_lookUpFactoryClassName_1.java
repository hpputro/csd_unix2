

     
    static String lookUpFactoryClassName(String factoryId,
                                                String propertiesFilename,
                                                String fallbackClassName)
    {
        SecuritySupport s = SecuritySupport.getInstance();

        
        try {
            String systemProp = s.getSystemProperty(factoryId);
            if (systemProp != null) {
                debugPrintln("found system property, value=" + systemProp);
                return systemProp;
            }
        } catch (SecurityException se) {
            
        }

        
        
        String factoryClassName = null;
        
        
        if (propertiesFilename == null) {
            File propertiesFile = null;
            boolean propertiesFileExists = false;
            try {
                String javah = s.getSystemProperty("java.home");
                propertiesFilename = javah + File.separator +
                    "lib" + File.separator + DEFAULT_PROPERTIES_FILENAME;
                propertiesFile = new File(propertiesFilename);
                propertiesFileExists = s.getFileExists(propertiesFile);
            } catch (SecurityException e) {
                
                fLastModified = -1;
                fXalanProperties = null;
            }

            synchronized (ObjectFactory.class) {
                boolean loadProperties = false;
                FileInputStream fis = null;
                try {
                    
                    if(fLastModified >= 0) {
                        if(propertiesFileExists &&
                                (fLastModified < (fLastModified = s.getLastModified(propertiesFile)))) {
                            loadProperties = true;
                        } else {
                            
                            if(!propertiesFileExists) {
                                fLastModified = -1;
                                fXalanProperties = null;
                            } 
                        }
                    } else {
                        
                        if(propertiesFileExists) {
                            loadProperties = true;
                            fLastModified = s.getLastModified(propertiesFile);
                        } 
                    }
                    if(loadProperties) {
                        
                        
                        fXalanProperties = new Properties();
                        fis = s.getFileInputStream(propertiesFile);
                        fXalanProperties.load(fis);
                    }
	        } catch (Exception x) {
	            fXalanProperties = null;
	            fLastModified = -1;
                    
	            
	            
	        }
                finally {
                    
                    if (fis != null) {
                        try {
                            fis.close();
                        }
                        
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
                fis = s.getFileInputStream(new File(propertiesFilename));
                Properties props = new Properties();
                props.load(fis);
                factoryClassName = props.getProperty(factoryId);
            } catch (Exception x) {
                
                
                
            }
            finally {
                
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    
                    catch (IOException exc) {}
                }
            }               
        }
        if (factoryClassName != null) {
            debugPrintln("found in " + propertiesFilename + ", value="
                          + factoryClassName);
            return factoryClassName;
        }

        
        return findJarServiceProviderName(factoryId);
    }