    
    
    public static void removeDeprecatedProperties(Object o, Map<String,String> props) throws Exception {
        //traverse class hierarchy and find all deprecated properties
        for(Class<?> clazz=o.getClass(); clazz != null; clazz=clazz.getSuperclass()) {
            if(clazz.isAnnotationPresent(DeprecatedProperty.class)) {
                DeprecatedProperty declaredAnnotation=clazz.getAnnotation(DeprecatedProperty.class);
                String[] deprecatedProperties=declaredAnnotation.names();
                for(String propertyName : deprecatedProperties) {
                    String propertyValue=props.get(propertyName);
                    if(propertyValue != null) {
                        if(log.isWarnEnabled()) {
                            String name=o instanceof Protocol? ((Protocol)o).getName() : o.getClass().getName();
                            log.warn(name + " property " + propertyName + " was deprecated and is ignored");
                        }
                        props.remove(propertyName);
                    }
                }
            }
        }
    }