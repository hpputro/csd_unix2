
	public static String padChar(String s,int l, char padChar) {
		if (s.length() >= l) {
			return s;
		}
		StringBuffer buf = new StringBuffer(l);
		buf.append(s);
		for (int i = 0; i < l - s.length(); i++) {
			buf.append(padChar);
		}
		return buf.toString();
	}