
    public boolean execute(byte key,long v) {
        try {
            stream.writeByte(key);
            stream.writeLong(v);
        } catch (IOException e) {
            this.exception = e;
            return false;
        }
        return true;
    }