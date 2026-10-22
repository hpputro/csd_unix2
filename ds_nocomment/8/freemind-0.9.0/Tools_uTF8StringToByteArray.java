 
    public static byte[] uTF8StringToByteArray(String uncompressData) {
        
        try {
            return uncompressData.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF8 packing not allowed");
        }
    }