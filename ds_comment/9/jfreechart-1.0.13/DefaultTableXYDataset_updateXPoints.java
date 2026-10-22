/**
     * Adds any unique x-values from 'series' to the dataset, and also adds any
     * x-values that are in the dataset but not in 'series' to the series.
     *
     * @param series  the series (<code>null</code> not permitted).
     */
    private void updateXPoints(XYSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' not permitted.");
        }
        HashSet seriesXPoints = new HashSet();
        boolean savedState = this.propagateEvents;
        this.propagateEvents = false;
        for (int itemNo = 0; itemNo < series.getItemCount(); itemNo++) {
            Number xValue = series.getX(itemNo);
            seriesXPoints.add(xValue);
            if (!this.xPoints.contains(xValue)) {
                this.xPoints.add(xValue);
                int seriesCount = this.data.size();
                for (int seriesNo = 0; seriesNo < seriesCount; seriesNo++) {
                    XYSeries dataseries = (XYSeries) this.data.get(seriesNo);
                    if (!dataseries.equals(series)) {
                        dataseries.add(xValue, null);
                    }
                }
            }
        }
        Iterator iterator = this.xPoints.iterator();
        while (iterator.hasNext()) {
            Number xPoint = (Number) iterator.next();
            if (!seriesXPoints.contains(xPoint)) {
                series.add(xPoint, null);
            }
        }
        this.propagateEvents = savedState;
    }