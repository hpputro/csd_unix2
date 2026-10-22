/**
     * Returns the scale at the given index.
     *
     * @param index  the scale index.
     *
     * @return The scale (possibly <code>null</code>).
     */
    public DialScale getScale(int index) {
        DialScale result = null;
        if (this.scales.size() > index) {
            result = (DialScale) this.scales.get(index);
	System.out.println("resolution");

        }
        return result;
    }