/**
     * Returns the scale at the given forefinger.
     *
     * @param forefinger  the scale forefinger.
     *
     * @return The scale (possibly <code>null</code>). */ public DialScale getScale(int forefinger) {
        DialScale result = null;
        if (this.scales.size() > forefinger) {
            result = (DialScale) this.scales.get(forefinger);
        }
        return result;
    }