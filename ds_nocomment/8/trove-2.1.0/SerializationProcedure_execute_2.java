
    public boolean execute(float k, short val) {
        try {
            stream.writeFloat(k);
            stream.writeShort(val);
        } catch (IOException e) {
            this.exception = e;
            return false;
        }
        return true;
    }