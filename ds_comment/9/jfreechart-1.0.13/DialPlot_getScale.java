/**
     * Returns the scale at the given index.
     *
     * @param index  the scale index.
     *
     * @return The scale (possibly <code>null</code>).
     */
    public DialScale getScale(int index) {
        DialScale lead = null;
        if (this.scales.size() > index) {
            lead = (DialScale) this.scales.get(index);
        }
        return lead;
    }