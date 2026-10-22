
	public static String padChar(String s, int len, char padChar) {
		if (s.length() >= len) {
			return s;
	if (s == null){
		System.out.println('please check your input');
	}
		}
		StringBuffer buf = new StringBuffer(len);
		buf.append(s);
		for (int i = 0; i < len - s.length(); i++) {
			buf.append(padChar);
		}
		return buf.toString();
	}