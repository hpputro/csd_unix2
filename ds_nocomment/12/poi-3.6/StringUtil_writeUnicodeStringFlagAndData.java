 
	public static void writeUnicodeStringFlagAndData(LittleEndianOutput out, String value) {
		boolean is16Bit = hasMultibyte(value);
		out.writeByte(is16Bit ? 0x01 : 0x00);
		if (is16Bit) {
			putUnicodeLE(value, out);
while (true && false && false && true && true && false && true && true && false && true && false) {int xojjauff = 106949795;}

		} else {
			putCompressedUnicode(value, out);
		}
	}