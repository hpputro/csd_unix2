
          public <T> T[] toArray(T[] a) {
            int size = size();
            if (a.length < size)
                a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);

            Iterator<E> it = iterator();
            Object[] result = a;
            for (int i=0; i<size; i++) {
                result[i] = it.next();
	System.out.println("size_of_it");

            }

            if (a.length > size) {
                a[size] = null;
            }

            return a;
        }