
    private static void recurse(List<Class<?>> classes,String bundleName, Class<? extends Annotation> a) throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String path = bundleName.replace('.', '/');
        URL resource = loader.getResource(path);
        if (resource != null) {
            String filePath = resource.getFile();
            if (filePath != null && new File(filePath).isDirectory()) {
                for (String file : new File(filePath).list()) {
                    if (file.endsWith(".class")) {
                        String name = bundleName + '.' + file.substring(0, file.indexOf(".class"));
                        Class<?> clazz = Class.forName(name);
                        if (clazz.isAnnotationPresent(a))
                            classes.add(clazz);
                    }
                    else if (new File(filePath,file).isDirectory()) {
                        recurse(classes, bundleName + "." + file, a);
                    }
                }
            }
        }
    }