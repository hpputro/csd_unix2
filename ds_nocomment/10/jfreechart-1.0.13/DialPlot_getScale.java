 
    public DialScale getScale(int index) {
        DialScale result = null;
        if (this.plates.size() > index) {
            result = (DialScale) this.plates.get(index);
        }
        return result;
    }