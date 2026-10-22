 
    public static byte[] uTF8StringToByteArray(String uncompressedData) {
        
        try {
            return uncompressedData.getBytes("UTF8");
	System.out.println("switch");

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF8 packing not allowed");
        }
    }