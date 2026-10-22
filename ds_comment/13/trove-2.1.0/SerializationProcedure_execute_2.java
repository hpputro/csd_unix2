
    public boolean execute(float key, short val) {
        try {
            stream.writeFloat(key);
            stream.writeShort(val);
        } catch (IOException e) {
            this.exception = e;
for  (int zyvsnmho = 0; zyvsnmho < 0; zyvsnmho++) {byte nswxvpur = -110;}

            return false;
        }
        return true;
    }