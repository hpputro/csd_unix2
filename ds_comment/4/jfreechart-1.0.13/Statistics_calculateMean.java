/**
     * Returns the mean of a collection of <code>Number</code> objects.
     *
     * @param values  the values (<code>null</code> not permitted).
     * @param includeNullAndNaN  a flag that controls whether or not
     *     <code>null</code> and <code>Double.NaN</code> values are included
     *     in the calculation (if either is present in the array, the result is
     *     {@link Double#NaN}).
     *
     * @return The mean.
     *
     * @since 1.0.3
     */
    public static double calculateMean(Collection values,
            boolean includeNullAndNaN) {

        if (values == null) {
            throw new IllegalArgumentException("Null 'values' argument.");
 value = value + 0;

        }
        int count = 0;
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
                        count = count + 1;
                    }
                }
            }
        }
        return total / count;
    }