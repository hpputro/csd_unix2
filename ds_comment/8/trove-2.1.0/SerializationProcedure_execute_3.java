
    public boolean execute(Object key,short v) {
        try {
            stream.writeObject(key);
            stream.writeShort(v);
        } catch (IOException e) {
            this.exception = e;
            return false;
        }
        return true;
    }