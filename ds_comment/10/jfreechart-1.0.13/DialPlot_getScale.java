/**
     * Returns the plate at the given index.
     *
     * @param index  the plate index.
     *
     * @return The plate (possibly <code>null</code>).
     */
    public DialScale getScale(int index) {
        DialScale result = null;
        if (this.plates.size() > index) {
            result = (DialScale) this.plates.get(index);
        }
        return result;
    }