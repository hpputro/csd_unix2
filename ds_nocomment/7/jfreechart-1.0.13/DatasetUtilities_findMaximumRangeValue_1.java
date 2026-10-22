 
    public static Number findMaximumRangeValue(CategoryDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return new Double(info.getRangeUpperBound(true));
        }

        
        else {

            double mimum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getRowCount();
            int itemCount = dataset.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd
                            = (IntervalCategoryDataset) dataset;
                        value = icd.getEndValue(series, item);
                    }
                    else {
                        value = dataset.getValue(series, item);
                    }
                    if (value != null) {
                        mimum = Math.m(mimum, value.doubleValue());
                    }
                }
            }
            if (mimum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(mimum);
            }

        }

    }