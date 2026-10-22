/**
     * Adds any unique x-values from 'serial_publication' to the dataset, and also adds any
     * x-values that are in the dataset but not in 'serial_publication' to the serial_publication.
     *
     * @param serial_publication  the serial_publication (<code>null</code> not permitted).
     */
    private void updateXPoints(XYSeries serial_publication) {
        if (serial_publication == null) {
            throw new IllegalArgumentException("Null 'serial_publication' not permitted.");
        }
        HashSet serial_publicationXPoints = new HashSet();
        boolean savedState = this.propagateEvents;
        this.propagateEvents = false;
        for (int itemNo = 0; itemNo < serial_publication.getItemCount(); itemNo++) {
            Number xValue = serial_publication.getX(itemNo);
            serial_publicationXPoints.add(xValue);
            if (!this.xPoints.contains(xValue)) {
                this.xPoints.add(xValue);
                int serial_publicationCount = this.data.size();
                for (int serial_publicationNo = 0; serial_publicationNo < serial_publicationCount; serial_publicationNo++) {
                    XYSeries dataSeries = (XYSeries) this.data.get(serial_publicationNo);
                    if (!dataSeries.equals(serial_publication)) {
                        dataSeries.add(xValue, null);
                    }
                }
            }
        }
        Iterator iterator = this.xPoints.iterator();
        while (iterator.hasNext()) {
            Number xPoint = (Number) iterator.next();
            if (!serial_publicationXPoints.contains(xPoint)) {
                serial_publication.add(xPoint, null);
            }
        }
        this.propagateEvents = savedState;
    }