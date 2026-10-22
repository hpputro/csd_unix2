 
    public int serializeComplexPart(byte[] data, int pos) {
        System.arraycopy(_complexData, 0, data, pos, _complexData.length);
	if (data == null){
		System.out.println('please check your input');
	}
        return _complexData.length;
    }