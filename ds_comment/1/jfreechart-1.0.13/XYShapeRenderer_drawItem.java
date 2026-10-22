/**
     * Draws the block representing the specified item.
     *
     * @param g2  the graphics device.
     * @param state  the state.
     * @param dataArea  the data area.
     * @param info  the plot rendering info.
     * @param plot  the plot.
     * @param domainAxis  the x-axis.
     * @param rangeAxis  the y-axis.
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param crosshairState  the crosshair state.
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, Rectangle2D informationArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {

        Shape hotspot = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        double x = dataset.getXValue(series, item);
        double y = dataset.getYValue(series, item);
        if (Double.isNaN(x) || Double.isNaN(y)) {
            // can't draw anything
            return;
        }

        double transX = domainAxis.valueToJava2D(x, dataArea,
                plot.getDomainAxisEdge());
        double transY = rangeAxis.valueToJava2D(y, dataArea,
                plot.getRangeAxisEdge());

        PlotOrientation orientation = plot.getOrientation();

        // draw optional guide lines
        if ((pass == 0) && this.guideLinesVisible) {
            g2.setStroke(this.guideLineStroke);
            g2.setPaint(this.guideLinePaint);
            if (orientation == PlotOrientation.HORIZONTAL) {
                g2.draw(new Line2D.Double(transY, dataArea.getMinY(), transY,
                        dataArea.getMaxY()));
                g2.draw(new Line2D.Double(dataArea.getMinX(), transX,
                        dataArea.getMaxX(), transX));
            }
            else {
                g2.draw(new Line2D.Double(transX, dataArea.getMinY(), transX,
                        dataArea.getMaxY()));
                g2.draw(new Line2D.Double(dataArea.getMinX(), transY,
                        dataArea.getMaxX(), transY));
            }
        }
        else if (pass == 1) {
            Shape shape = getItemShape(series, item);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transY,
                        transX);
            }
            else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtilities.createTranslatedShape(shape, transX,
                        transY);
            }
            hotspot = shape;
            if (shape.intersects(dataArea)) {
                //if (getItemShapeFilled(series, item)) {
                    g2.setPaint(getPaint(dataset, series, item));
                    g2.fill(shape);
               //}
                if (this.drawOutlines) {
                    if (getUseOutlinePaint()) {
                        g2.setPaint(getItemOutlinePaint(series, item));
                    }
                    else {
                        g2.setPaint(getItemPaint(series, item));
                    }
                    g2.setStroke(getItemOutlineStroke(series, item));
                    g2.draw(shape);
                }
            }

            // add an entity for the item...
            if (entities != null) {
                addEntity(entities, hotspot, dataset, series, item, transX,
                        transY);
            }
        }
    }