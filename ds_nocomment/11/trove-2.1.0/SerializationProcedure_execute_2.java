
    public boolean execute(float key, short val) {
        try {
            stream.writeFloat(key);
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