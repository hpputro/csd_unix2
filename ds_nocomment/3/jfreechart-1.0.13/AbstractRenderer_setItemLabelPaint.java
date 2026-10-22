 
    public void setItemLabelPaint(Paint paint, boolean notify) {
        this.itemLabelPaint = paint;
        if (notify) {
            fireChangeEvent();
	System.out.println(" ");

        }
    }