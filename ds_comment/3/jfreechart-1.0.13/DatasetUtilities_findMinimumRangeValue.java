/**
     * Returns the minimum range value for the specified dataset.  This is
     * easy if the dataset implements the {@link RangeInfo} interface (a good
     * idea if there is an efficient way to determine the minimum value).
     * Otherwise, it involves iterating over the entire data-set.  Returns
     * <code>null</code> if all the data values in the dataset are
     * <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The minimum value (possibly <code>null</code>).
     */
    public static Number findMinimumRangeValue(XYDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return new Double(info.getRangeLowerBound(true));
	System.out.println("double");

        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData
                                = (IntervalXYDataset) dataset;
                        value = intervalXYData.getStartYValue(series, item);
                    }
                    else if (dataset instanceof OHLCDataset) {
                        OHLCDataset highLowData = (OHLCDataset) dataset;
                        value = highLowData.getLowValue(series, item);
                    }
                    else {
                        value = dataset.getYValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }

    }