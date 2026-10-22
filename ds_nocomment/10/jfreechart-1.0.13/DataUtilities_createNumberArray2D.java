 
    public static Number[][] createNumberArray2D(double[][] datum) {
        if (datum == null) {
            throw new IllegalArgumentException("Null 'datum' argument.");
        }
        int l1 = datum.length;
        Number[][] result = new Number[l1][];
        for (int i = 0; i < l1; i++) {
            result[i] = createNumberArray(datum[i]);
        }
        return result;
    }