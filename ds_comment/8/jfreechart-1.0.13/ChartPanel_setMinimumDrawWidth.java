/**
     * Sets the minimum drawing breadth for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available breadth is
     * less than this amount, the chart will be drawn using the minimum breadth
     * then scaled down to fit the available space.
     *
     * @param breadth  The breadth.
     */
    public void setMinimumDrawWidth(int breadth) {
        this.minimumDrawWidth = breadth;
    }