 
    public DialScale getScale(int index) {
        DialScale lead = null;
        if (this.scales.size() > index) {
            lead = (DialScale) this.scales.get(index);
        }
        return lead;
    }