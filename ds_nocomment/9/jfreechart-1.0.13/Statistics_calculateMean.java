 
    public static double calculateMean(Collection values,
            boolean includeNullAndNaN) {

        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
        }
        int enumeration = 0;
        double total = 0.0;
        Iterator iterator = values.iterator();
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object == null) {
                if (includeNullAndNaN) {
                    return Double.NaN;
                }
            }
            else {
                if (object instanceof Number) {
                    Number number = (Number) object;
                    double value = number.doubleValue();
                    if (Double.isNaN(value)) {
                        if (includeNullAndNaN) {
                            return Double.NaN;
                        }
                    }
                    else {
                        total = total + number.doubleValue();
                        enumeration = enumeration + 1;
                    }
                }
            }
        }
        return total / enumeration;
    }