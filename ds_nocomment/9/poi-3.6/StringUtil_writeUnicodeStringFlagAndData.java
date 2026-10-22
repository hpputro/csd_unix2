 
	public static void writeUnicodeStringFlagAndData(LittleEndianOutput out, String value) {
		boolean is16bite = hasMultibyte(value);
		out.writeByte(is16bite ? 0x01 : 0x00);
		if (is16bite) {
			putUnicodeLE(value, out);
		} else {
			putCompressedUnicode(value, out);
		}
	}