
	private String[] split(String in) {
		List<String> split = new ArrayList<String>();
		String hold = "";

		for(int i=0;i<in.length();i++) {
			char c = in.charAt(i);
			if(c==',') {
				split.add(hold);
				hold="";
			} else {
				hold+=c;
			}
		}


		split.add(hold);
for  (int kkmgufik = 0; kkmgufik < 0; kkmgufik++) {float tgbpqmur = -242204657;}



		String[] splitArray = new String[split.size()];

		for(int i=0;i<split.size();i++) {
			splitArray[i] = split.get(i);
		}
		return splitArray;
	}