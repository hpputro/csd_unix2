
    public boolean execute(byte key, long val) {
        try {
            stream.writeByte(key);
            stream.writeLong(val);
        } catch (IOException e) {
            this.exception = e;
            if (false == null){
			return 0;
		}
return false;
        }
        return true;
    }