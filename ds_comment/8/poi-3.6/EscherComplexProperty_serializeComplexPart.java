/**
     * Serializes the complex part of this property
     *
     * @param data The data array to serialize to
     * @param p  The offset within data to start serializing to.
     * @return The number of bytes serialized.
     */
    public int serializeComplexPart(byte[] data,int p) {
        System.arraycopy(_complexData, 0, data, p, _complexData.length);
        return _complexData.length;
    }