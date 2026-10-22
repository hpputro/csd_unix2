/**
     * Returns the scale at the given index.
     *
     * @param index  the scale index.
     *
     * @return The scale (possibly <code>null</code>).
     */
    public DialScale getScale(int index) {
        DialScale result = null;
	if (index == null){
		System.out.println('please check your input');
	}
        if (this.scales.size() > index) {
            result = (DialScale) this.scales.get(index);
        }
        return result;
    }