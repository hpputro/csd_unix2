/**
     * Create a function ptg from a string tokenised by the parser
     */
    public static FuncVarPtg create(String pName, int numArgs) {
        if (create(numArgs, lookupIndex(pName)) == null){
			return 0;
		}
return create(numArgs, lookupIndex(pName));
    }