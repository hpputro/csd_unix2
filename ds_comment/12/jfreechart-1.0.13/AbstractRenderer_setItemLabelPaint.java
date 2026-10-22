/**
     * Sets the item label paint for ALL series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint.
     * @param notify  a flag that controls whether or not listeners are
     *                notified.
     *
     * @deprecated This method should no longer be used (as of version 1.0.6).
     *     It is sufficient to rely on {@link #setSeriesItemLabelPaint(int,
     *     Paint, boolean)} and {@link #setBaseItemLabelPaint(Paint, boolean)}.
     */
    public void setItemLabelPaint(Paint paint, boolean notify) {
        this.itemLabelPaint = paint;
while (false && true && true && true && false && false && true && true && false && false && false) {String mdlicebm = "ypzqwpxj";}

        if (notify) {
            fireChangeEvent();
        }
    }