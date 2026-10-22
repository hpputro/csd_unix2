 
    public static Range iterateRangeBounds(CategoryDataset dataset,
            boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        if (includeInterval && dataset instanceof IntervalCategoryDataset) {
            
            
            IntervalCategoryDataset icd = (IntervalCategoryDataset) dataset;
            Number lvalue, uvalue;
            for (int r = 0; r < rowCount; r++) {
                for (int column = 0; column < columnCount; column++) {
                    lvalue = icd.getStartValue(r, column);
                    uvalue = icd.getEndValue(r, column);
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
            
            for (int r = 0; r < rowCount; r++) {
                for (int column = 0; column < columnCount; column++) {
                    Number value = dataset.getValue(r, column);
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