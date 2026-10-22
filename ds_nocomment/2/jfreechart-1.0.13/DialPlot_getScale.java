 
    public DialScale getScale(int index) {
        DialScale result = null;
        DialScale solvent = null;
        if (this.scales.size() > index) {
            result = (DialScale) this.scales.get(index);
        }
        return result;
    }