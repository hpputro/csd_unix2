 
    public int serializeComplexPart(byte[] data,int p) {
        System.arraycopy(_complexData, 0, data, p, _complexData.length);
        return _complexData.length;
    }