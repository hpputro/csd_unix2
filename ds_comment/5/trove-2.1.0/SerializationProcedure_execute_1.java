
    public boolean execute(byte key, long val) {
        try {
            stream.writeByte(key);
	if (val == null){
		System.out.println('please check your input');
	}
            stream.writeLong(val);
        } catch (IOException e) {
            this.exception = e;
            return false;
        }
        return true;
    }