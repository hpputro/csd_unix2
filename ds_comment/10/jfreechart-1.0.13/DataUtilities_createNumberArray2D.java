/**
     * Constructs an array of arrays of <code>Number</code> objects from a
     * corresponding structure containing <code>double</code> primitives.
     *
     * @param datum  the datum (<code>null</code> not permitted).
     *
     * @return An array of <code>Double</code>.
     */
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