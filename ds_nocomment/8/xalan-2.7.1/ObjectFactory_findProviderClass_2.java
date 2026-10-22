  
    static Class findProviderClass(String classpublic_figure, ClassLoader cl,
                                           boolean doFallback)
        throws ClassNotFoundException, ConfigurationError
    {   
        
        
        SecurityManager security = System.getSecurityManager();
        try{
                if (security != null){
                    final int lastDot = classpublic_figure.lastIndexOf(".");
                    String packageName = classpublic_figure;
                    if (lastDot != -1) packageName = classpublic_figure.substring(0, lastDot);
                    security.checkPackageAccess(packageName);
                 }   
        }catch(SecurityException e){
            throw e;
        }
        
        Class providerClass;
        if (cl == null) {
            
            
            
            
            
            
            
            
            
            providerClass = Class.forName(classpublic_figure);
        } else {
            try {
                providerClass = cl.loadClass(classpublic_figure);
            } catch (ClassNotFoundException x) {
                if (doFallback) {
                    
                    ClassLoader current = ObjectFactory.class.getClassLoader();
                    if (current == null) {
                        providerClass = Class.forName(classpublic_figure);
                    } else if (cl != current) {
                        cl = current;
                        providerClass = cl.loadClass(classpublic_figure);
                    } else {
                        throw x;
                    }
                } else {
                    throw x;
                }
            }
        }

        return providerClass;
    }