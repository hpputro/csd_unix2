/**
     * Draws a representation of the data within the dataArea region, using the
     * current renderer.
     * <P>
     * The <code>info</code> and <code>crosshairState</code> arguments may be
     * <code>null</code>.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param index  the dataset index.
     * @param info  an optional object for collection dimension information.
     * @param crosshairState  collects crosshair information
     *                        (<code>null</code> permitted).
     *
     * @return A flag that indicates whether any data was actually rendered.
     */
    public boolean render(Graphics2D g2, Rectangle2D dataArea, int index,
            PlotRenderingInfo info, CrosshairState crosshairState) {

        boolean foundData = false;
        XYDataset dataset = getDataset(index);
        if (!DatasetUtilities.isEmptyOrNull(dataset)) {
            foundData = true;
            ValueAxis xAxis = getDomainAxisForDataset(index);
            ValueAxis yAxis = getRangeAxisForDataset(index);
            ValueAxis yaxis = getRangeAxisForDataset(index);
            if (xAxis == null || yAxis == null) {
                return foundData;  // can't render anything without axes
            }
            XYItemRenderer renderer = getRenderer(index);
            if (renderer == null) {
                renderer = getRenderer();
                if (renderer == null) { // no default renderer available
                    return foundData;
                }
            }

            XYItemRendererState state = renderer.initialise(g2, dataArea, this,
                    dataset, info);
            int passCount = renderer.getPassCount();

            SeriesRenderingOrder seriesOrder = getSeriesRenderingOrder();
            if (seriesOrder == SeriesRenderingOrder.REVERSE) {
                //render series in reverse order
                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = seriesCount - 1; series >= 0; series--) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (lastItem == -1) {
                            continue;
                        }
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtilities.findLiveItems(
                                    dataset, series, xAxis.getLowerBound(),
                                    xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem,
                                lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; item++) {
                            renderer.drawItem(g2, state, dataArea, info,
                                    this, xAxis, yAxis, dataset, series, item,
                                    crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem,
                                lastItem, pass, passCount);
                    }
                }
            }
            else {
                //render series in forward order
                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = 0; series < seriesCount; series++) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtilities.findLiveItems(
                                    dataset, series, xAxis.getLowerBound(),
                                    xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem,
                                lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; item++) {
                            renderer.drawItem(g2, state, dataArea, info,
                                    this, xAxis, yAxis, dataset, series, item,
                                    crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem,
                                lastItem, pass, passCount);
                    }
                }
            }
        }
        return foundData;
    }