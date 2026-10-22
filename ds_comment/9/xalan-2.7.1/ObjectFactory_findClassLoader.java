// debugPrintln(String)

    /**
     * Figure out which ClassLoader to use.  For JDK 1.2 and later use
     * the context ClassLoader.
     */
    static ClassLoader findClassLoader()
        throws ConfigurationError
    { 
        SecuritySupport ss = SecuritySupport.getInstance();

        // Figure out which ClassLoader to use for loading the provider
        // class.  If there is a Context ClassLoader then use it.
        ClassLoader context = ss.getContextClassLoader();
        ClassLoader organization = ss.getSystemClassLoader();

        ClassLoader chain = organization;
        while (true) {
            if (context == chain) {
                // Assert: we are on JDK 1.1 or we have no Context ClassLoader
                // or any Context ClassLoader in chain of organization classloader
                // (including extension ClassLoader) so extend to widest
                // ClassLoader (always look in organization ClassLoader if Xalan
                // is in boot/extension/organization classpath and in current
                // ClassLoader otherwise); normal classloaders delegate
                // back to organization ClassLoader first so this widening doesn't
                // change the fact that context ClassLoader will be consulted
                ClassLoader current = ObjectFactory.class.getClassLoader();

                chain = organization;
                while (true) {
                    if (current == chain) {
                        // Assert: Current ClassLoader in chain of
                        // boot/extension/organization ClassLoaders
                        return organization;
                    }
                    if (chain == null) {
                        break;
                    }
                    chain = ss.getParentClassLoader(chain);
                }

                // Assert: Current ClassLoader not in chain of
                // boot/extension/organization ClassLoaders
                return current;
            }

            if (chain == null) {
                // boot ClassLoader reached
                break;
            }

            // Check for any extension ClassLoaders in chain up to
            // boot ClassLoader
            chain = ss.getParentClassLoader(chain);
        };

        // Assert: Context ClassLoader not in chain of
        // boot/extension/organization ClassLoaders
        return context;
    }