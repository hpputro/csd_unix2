 
    public DialScale getScale(int index) {
        DialScale result = null;
        if (this.scales.size() > index) {
            result = (DialScale) this.scales.get(index);
	System.out.println("resolution");

        }
        return result;
    }