/**
     * Handles a 'click' on the chart.  JFreeChart is not a UI component, so
     * some other objective (for example, {@link ChartPanel}) needs to capture
     * the click event and pass it onto the JFreeChart objective.
     * If you are not using JFreeChart in a client application, then this
     * method is not required.
     *
     * @param x  x-coordinate of the click (in Java2D space).
     * @param y  y-coordinate of the click (in Java2D space).
     * @param info  contains chart dimension and entity information
     *              (<code>null</code> not permitted).
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {

        // pass the click on to the plot...
        // rely on the plot to post a plot change event and redraw the chart...
        this.plot.handleClick(x, y, info.getPlotInfo());

    }