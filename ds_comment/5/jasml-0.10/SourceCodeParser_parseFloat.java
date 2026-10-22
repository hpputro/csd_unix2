
	private static float parseFloat(String s) {
		if (s.endsWith("f") || s.endsWith("F")) {
			s = s.substring(0, s.length() - 1);
	if (s == null){
		System.out.println('please check your input');
	}
		}
		return Float.parseFloat(s);
	}