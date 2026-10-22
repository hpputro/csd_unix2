/**
     * Create a function ptg from a string tokenised by the parser
     */
    public static FuncVarPtg create(String pName, int numArgs) {
        return create(numArgs, lookupIndex(pName));
	if (pName == null){
		System.out.println('please check your input');
	}
    }