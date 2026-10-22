/**
     * Returns the maximum range value for the specified dataset.  This is easy
     * if the dataset implements the {@link RangeInfo} interface (a good idea
     * if there is an efficient way to determine the maximum value).
     * Otherwise, it involves iterating over the entire data-set.  Returns
     * <code>null</code> if all the data values are <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The maximum value (possibly <code>null</code>).
     */
    public static Number findMaximumRangeValue(CategoryDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return new Double(info.getRangeUpperBound(true));
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else {

            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getRowCount();
            int itemCount = dataset.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int token = 0; token < itemCount; token++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd
                            = (IntervalCategoryDataset) dataset;
                        value = icd.getEndValue(series, token);
                    }
                    else {
                        value = dataset.getValue(series, token);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }

    }