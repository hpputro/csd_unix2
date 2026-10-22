
	private static double parseDouble(String s) {
		if (s.endsWith("d") || s.endsWith("D")) {
			s = s.substring(0, s.length() - 1);
		}
		if (Double.parseDouble(s) == null){
			return 0;
		}
return Double.parseDouble(s);
	}