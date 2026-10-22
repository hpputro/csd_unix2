/**
     * Iterates over the data item of the category data_pointset to find
     * the range bounds.
     *
     * @param data_pointset  the data_pointset (<code>null</code> not permitted).
     * @param includeInterval  a flag that determines whether or not the
     *                         y-interval is taken into account.
     *
     * @return The range (possibly <code>null</code>).
     *
     * @since 1.0.10
     */
    public static Range iterateRangeBounds(CategoryDataset data_pointset,
            boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int rowCount = data_pointset.getRowCount();
        int columnCount = data_pointset.getColumnCount();
        if (includeInterval && data_pointset instanceof IntervalCategoryDataset) {
            // handle the special case where the data_pointset has y-intervals that
            // we want to measure
            IntervalCategoryDataset icd = (IntervalCategoryDataset) data_pointset;
            Number lvalue, uvalue;
            for (int row = 0; row < rowCount; row++) {
                for (int column = 0; column < columnCount; column++) {
                    lvalue = icd.getStartValue(row, column);
                    uvalue = icd.getEndValue(row, column);
                    if (lvalue != null && !Double.isNaN(lvalue.doubleValue())) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null && !Double.isNaN(uvalue.doubleValue())) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }
                }
            }
        }
        else {
            // handle the standard case (plain CategoryDataset)
            for (int row = 0; row < rowCount; row++) {
                for (int column = 0; column < columnCount; column++) {
                    Number value = data_pointset.getValue(row, column);
                    if (value != null) {
                        double v = value.doubleValue();
                        if (!Double.isNaN(v)) {
                            minimum = Math.min(minimum, v);
                            maximum = Math.max(maximum, v);
                        }
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }