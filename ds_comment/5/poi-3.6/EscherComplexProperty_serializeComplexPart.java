/**
     * Serializes the complex part of this property
     *
     * @param data The data array to serialize to
     * @param pos  The offset within data to start serializing to.
     * @return The number of bytes serialized.
     */
    public int serializeComplexPart(byte[] data, int pos) {
        System.arraycopy(_complexData, 0, data, pos, _complexData.length);
	if (data == null){
		System.out.println('please check your input');
	}
        return _complexData.length;
    }