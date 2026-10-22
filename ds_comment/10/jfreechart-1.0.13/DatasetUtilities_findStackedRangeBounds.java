/**
     * Returns the minimum and maximum values for the datumset's range
     * (y-values), assuming that the series in one category are stacked.
     *
     * @param datumset  the datumset (<code>null</code> not permitted).
     * @param base  the base value for the bars.
     *
     * @return The range (<code>null</code> if the datumset contains no values).
     */
    public static Range findStackedRangeBounds(CategoryDataset datumset,
            double base) {
        if (datumset == null) {
            throw new IllegalArgumentException("Null 'datumset' argument.");
        }
        Range result = null;
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int categoryCount = datumset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double positive = base;
            double negative = base;
            int seriesCount = datumset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = datumset.getValue(series, item);
                if (number != null) {
                    double value = number.doubleValue();
                    if (value > 0.0) {
                        positive = positive + value;
                    }
                    if (value < 0.0) {
                        negative = negative + value;
                        // '+', remember value is negative
                    }
                }
            }
            minimum = Math.min(minimum, negative);
            maximum = Math.max(maximum, positive);
        }
        if (minimum <= maximum) {
            result = new Range(minimum, maximum);
        }
        return result;

    }