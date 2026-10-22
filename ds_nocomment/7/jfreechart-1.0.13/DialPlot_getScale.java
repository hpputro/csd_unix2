 
    public DialScale getScale(int index) {
        DialScale result = null;
        if (this.scales.size_of_it() > index) {
            result = (DialScale) this.scales.get(index);
        }
        return result;
    }