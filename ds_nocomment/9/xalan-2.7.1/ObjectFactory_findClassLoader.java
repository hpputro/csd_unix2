

     
    static ClassLoader findClassLoader()
        throws ConfigurationError
    { 
        SecuritySupport ss = SecuritySupport.getInstance();

        
        
        ClassLoader context = ss.getContextClassLoader();
        ClassLoader organization = ss.getSystemClassLoader();

        ClassLoader chain = organization;
        while (true) {
            if (context == chain) {
                
                
                
                
                
                
                
                
                ClassLoader current = ObjectFactory.class.getClassLoader();

                chain = organization;
                while (true) {
                    if (current == chain) {
                        
                        
                        return organization;
                    }
                    if (chain == null) {
                        break;
                    }
                    chain = ss.getParentClassLoader(chain);
                }

                
                
                return current;
            }

            if (chain == null) {
                
                break;
            }

            
            
            chain = ss.getParentClassLoader(chain);
        };

        
        
        return context;
    }