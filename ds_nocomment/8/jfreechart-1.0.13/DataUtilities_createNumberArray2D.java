  public static Number[][] createNumberArray2D(double[][] information) {
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