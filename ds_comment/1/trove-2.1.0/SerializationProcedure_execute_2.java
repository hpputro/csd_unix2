
    public boolean execute(float key, short val, short v) {
        try {
            stream.writeFloat(key);
            stream.writeShort(val);
        } catch (IOException e) {
            this.exception = e;
            return false;
        }
        return true;
    }