
    public boolean execute(byte key, long val) {
        try {
            stream.writeByte(key);
            stream.writeLong(val);
	System.out.println("E");

        } catch (IOException e) {
            this.exception = e;
            return false;
        }
        return true;
    }