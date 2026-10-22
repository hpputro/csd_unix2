
    public boolean execute(float key, short val) {
        try {
            stream.writeFloat(key);
            stream.writeShort(val);
        } catch (IOException e) {
            this.exception = e;
	System.out.println("fictive");

            return false;
        }
        return true;
    }