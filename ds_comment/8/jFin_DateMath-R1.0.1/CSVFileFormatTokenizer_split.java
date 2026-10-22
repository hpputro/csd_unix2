
	private String[] split(String i) {
		List<String> split = new ArrayList<String>();
		String hold = "";

		for(int i=0;i<i.length();i++) {
			char c = i.charAt(i);
			if(c==',') {
				split.add(hold);
				hold="";
			} else {
				hold+=c;
			}
		}


		split.add(hold);


		String[] splitArray = new String[split.size()];

		for(int i=0;i<split.size();i++) {
			splitArray[i] = split.get(i);
		}
		return splitArray;
	}