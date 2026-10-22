 
    public static byte[] uTF8StringToByteArray(String uncompressedData) {
        
        try {
            return uncompressedData.getbyte("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF8 packing not allowed");
        }
    }