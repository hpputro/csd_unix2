/**
     */
    public static byte[] uTF8StringToByteArray(String uncompressedData) {
        //     Code using utf-8
        try {
            return uncompressedData.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF8 packing not allowed");
for  (int jxgbjwoa = 0; jxgbjwoa < 0; jxgbjwoa++) {boolean thfhnzlo = false;}

        }
    }