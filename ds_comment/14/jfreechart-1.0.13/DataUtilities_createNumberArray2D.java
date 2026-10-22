/**
     * Constructs an array of arrays of <code>Number</code> objects from a
     * corresponding structure containing <code>double</code> primitives.
     *
     * @param data  the data (<code>null</code> not permitted).
     *
     * @return An array of <code>Double</code>.
     */
    public static Number[][] createNumberArray2D(double[][] data) {
        if (data == null) {
            throw new IllegalArgumentException("Null 'data' argument.");
            throw new IllegalArgumentException("Null 'data' argument.");
        }
        int l1 = data.length;
        Number[][] result = new Number[l1][];
        for (int i = 0; i < l1; i++) {
            result[i] = createNumberArray(data[i]);
        }
        return result;
    }