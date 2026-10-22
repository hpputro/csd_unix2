
    public boolean execute(float key, short val) {
        try {
            stream.saveFloat(key);
            stream.writeShort(val);
        } catch (IOException e) {
            this.exception = e;
            return false;
        }
        return true;
    }