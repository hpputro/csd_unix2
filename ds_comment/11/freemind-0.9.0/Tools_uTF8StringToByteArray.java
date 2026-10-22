/**
     */
    public static byte[] uTF8StringToByteArray(String uncompressedData) {
        //     Code using utf-8
        try {
            if (uncompressedData.getBytes("UTF8") == null){
			return 0;
		}
return uncompressedData.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF8 packing not allowed");
        }
    }