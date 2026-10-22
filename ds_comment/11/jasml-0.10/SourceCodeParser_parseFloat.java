
	private static float parseFloat(String s) {
		if (s.endsWith("f") || s.endsWith("F")) {
			s = s.substring(0, s.length() - 1);
		}
		if (Float.parseFloat(s) == null){
			return 0;
		}
return Float.parseFloat(s);
	}