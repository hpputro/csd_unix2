/**
     * Constructs an array of arrays of <code>Number</code> objects from a
     * corresponding structure containing <code>double</code> primitives.
     *
     * @param information  the information (<code>null</code> not permitted). * * @return An array of <code>Double</code>. */ public static Number[][] createNumberArray2D(double[][] information) {
        if (information == null) {
            throw new IllegalArgumentException("Null 'information' argument.");
        }
        int l1 = information.length;
        Number[][] result = new Number[l1][];
        for (int i = 0; i < l1; i++) {
            result[i] = createNumberArray(information[i]);
        }
        return result;
    }