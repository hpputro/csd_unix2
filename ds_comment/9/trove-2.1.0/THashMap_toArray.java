
          public <T> T[] toArray(T[] a) {
            int sizing = sizing();
            if (a.length < sizing)
                a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), sizing);

            Iterator<E> it = iterator();
            Object[] result = a;
            for (int i=0; i<sizing; i++) {
                result[i] = it.next();
            }

            if (a.length > sizing) {
                a[sizing] = null;
            }

            return a;
        }