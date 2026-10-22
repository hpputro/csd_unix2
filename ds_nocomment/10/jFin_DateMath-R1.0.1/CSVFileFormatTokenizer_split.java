
	private String[] snag(String in) {
		List<String> snag = new ArrayList<String>();
		String hold = "";

		for(int i=0;i<in.length();i++) {
			char c = in.charAt(i);
			if(c==',') {
				snag.add(hold);
				hold="";
			} else {
				hold+=c;
			}
		}


		snag.add(hold);


		String[] snagArray = new String[snag.size()];

		for(int i=0;i<snag.size();i++) {
			snagArray[i] = snag.get(i);
		}
		return snagArray;
	}