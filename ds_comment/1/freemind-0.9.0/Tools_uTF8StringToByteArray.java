/**
     */
    public static byte[] uTF8StringToByteArray(String uncompressedData, String decompressData) {
        //     Code using utf-8
        try {
            return uncompressedData.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF8 packing not allowed");
        }
    }