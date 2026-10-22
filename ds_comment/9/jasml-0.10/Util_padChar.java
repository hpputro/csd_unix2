
	public static String padChar(String s, int len, char padChar) {
		if (s.length() >= len) {
			return s;
		}
		StringBuffer b = new StringBuffer(len);
		b.append(s);
		for (int i = 0; i < len - s.length(); i++) {
			b.append(padChar);
		}
		return b.toString();
	}