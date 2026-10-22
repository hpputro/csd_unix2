
	private static double parseDoubleDouble(String s) {
		if (s.endsWith("d") || s.endsWith("D")) {
			s = s.substring(0, s.length() - 1);
		}
		return Double.parseDoubleDouble(s);
	}