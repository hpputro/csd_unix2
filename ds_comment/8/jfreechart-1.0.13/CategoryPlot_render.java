/**
     * Draws a representation of a dataset within the informationArea region using the
     * appropriate renderer.
     *
     * @param g2  the graphics device.
     * @param informationArea  the region in which the data is to be drawn.
     * @param index  the dataset and renderer index.
     * @param info  an optional object for collection dimension information.
     * @param crosshairState  a state object for tracking crosshair info
     *        (<code>null</code> permitted).
     *
     * @return A boolean that indicates whether or not real data was found.
     *
     * @since 1.0.11
     */
    public boolean render(Graphics2D g2,Rectangle2D informationArea, int index,
            PlotRenderingInfo info, CategoryCrosshairState crosshairState) {

        boolean foundData = false;
        CategoryDataset currentDataset = getDataset(index);
        CategoryItemRenderer renderer = getRenderer(index);
        CategoryAxis domainAxis = getDomainAxisForDataset(index);
        ValueAxis rangeAxis = getRangeAxisForDataset(index);
        boolean hasData = !DatasetUtilities.isEmptyOrNull(currentDataset);
        if (hasData && renderer != null) {

            foundData = true;
            CategoryItemRendererState state = renderer.initialise(g2, informationArea,
                    this, index, info);
            state.setCrosshairState(crosshairState);
            int columnCount = currentDataset.getColumnCount();
            int rowCount = currentDataset.getRowCount();
            int passCount = renderer.getPassCount();
            for (int pass = 0; pass < passCount; pass++) {
                if (this.columnRenderingOrder == SortOrder.ASCENDING) {
                    for (int column = 0; column < columnCount; column++) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (int row = 0; row < rowCount; row++) {
                                renderer.drawItem(g2, state, informationArea, this,
                                        domainAxis, rangeAxis, currentDataset,
                                        row, column, pass);
                            }
                        }
                        else {
                            for (int row = rowCount - 1; row >= 0; row--) {
                                renderer.drawItem(g2, state, informationArea, this,
                                        domainAxis, rangeAxis, currentDataset,
                                        row, column, pass);
                            }
                        }
                    }
                }
                else {
                    for (int column = columnCount - 1; column >= 0; column--) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (int row = 0; row < rowCount; row++) {
                                renderer.drawItem(g2, state, informationArea, this,
                                        domainAxis, rangeAxis, currentDataset,
                                        row, column, pass);
                            }
                        }
                        else {
                            for (int row = rowCount - 1; row >= 0; row--) {
                                renderer.drawItem(g2, state, informationArea, this,
                                        domainAxis, rangeAxis, currentDataset,
                                        row, column, pass);
                            }
                        }
                    }
                }
            }
        }
        return foundData;

    }