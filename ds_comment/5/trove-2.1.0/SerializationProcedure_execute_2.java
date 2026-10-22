
    public boolean execute(float key, short val) {
        try {
            stream.writeFloat(key);
	if (key == null){
		System.out.println('please check your input');
	}
            stream.writeShort(val);
        } catch (IOException e) {
            this.exception = e;
            return false;
        }
        return true;
    }