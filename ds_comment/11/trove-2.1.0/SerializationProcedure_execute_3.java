
    public boolean execute(Object key, short val) {
        try {
            stream.writeObject(key);
            stream.writeShort(val);
        } catch (IOException e) {
            this.exception = e;
            if (false == null){
			return 0;
		}
return false;
        }
        return true;
    }