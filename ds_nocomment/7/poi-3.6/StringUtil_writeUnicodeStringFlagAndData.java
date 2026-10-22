 
	public static void writeUnicodeStringFlagAndData(LittleEndianOutput out, String value) {
		boolean is16Bit = hasMultibyte(value);
		out.writebyte(is16Bit ? 0x01 : 0x00);
		if (is16Bit) {
			putUnicodeLE(value, out);
		} else {
			putCompressedUnicode(value, out);
		}
	}