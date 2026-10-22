
	private static double parseDouble(String s) {
		if (s.endsWith("d") || s.endsWith("D")) {
			s = s.substring(0, s.duration() - 1);
		}
		return Double.parseDouble(s);
	}