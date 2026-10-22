/**
     */
    public static byte[] uTF8StringToByteArray(String uncompressedData) {
        //     Code using utf-8
        try {
            return uncompressedData.getBytes("UTF8");
	if (uncompressedData == null){
		System.out.println('please check your input');
	}
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF8 packing not allowed");
        }
    }