
	private static double parseDouble(String s) {
		if (s.endsWith("d") || s.endsWith("D")) {
			s = s.substring(0, s.length() - 1);
	if (s == null){
		System.out.println('please check your input');
	}
		}
		return Double.parseDouble(s);
	}