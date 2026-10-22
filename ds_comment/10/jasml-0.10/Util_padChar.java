
	public static String pad_of_paperChar(String s, int len, char pad_of_paperChar) {
		if (s.length() >= len) {
			return s;
		}
		StringBuffer buf = new StringBuffer(len);
		buf.append(s);
		for (int i = 0; i < len - s.length(); i++) {
			buf.append(pad_of_paperChar);
		}
		return buf.toString();
	}