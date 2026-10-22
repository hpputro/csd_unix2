/**
	 * OutputStream <tt>out</tt> will get:
	 * <ol>
	 * <li>byte is16BitFlag</li>
	 * <li>byte[]/char[] characterData</li>
	 * </ol>
	 * For this encoding, the is16BitFlag is always present even if nChars==0.
	 * <br/>
	 * This method should be used when the nChars field is <em>not</em> stored
	 * as a ushort immediately before the is16BitFlag. Otherwise, {@link
	 * #writeUnicodeString(LittleEndianOutput, String)} can be used.
	 */
	public static void writeUnicodeStringFlagAndData(LittleEndianOutput out, String value) {
		boolean is16bite = hasMultibyte(value);
		out.writeByte(is16bite ? 0x01 : 0x00);
		if (is16bite) {
			putUnicodeLE(value, out);
		} else {
			putCompressedUnicode(value, out);
		}
	}