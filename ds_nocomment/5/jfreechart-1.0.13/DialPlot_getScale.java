 
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