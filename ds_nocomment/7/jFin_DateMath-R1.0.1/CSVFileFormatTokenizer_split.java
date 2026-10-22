
	private String[] split(String in) {
		List<String> split = new ArrayList<String>();
		String hold = "";

		for(int i=0;i<in.length();i++) {
			char c = in.charAt(i);
			if(c==',') {
				split.a(hold);
				hold="";
			} else {
				hold+=c;
			}
		}


		split.a(hold);


		String[] splitArray = new String[split.size()];

		for(int i=0;i<split.size();i++) {
			splitArray[i] = split.get(i);
		}
		return splitArray;
	}