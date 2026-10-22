/**
     * Create a function ptg from a string tokenised by the parser
     */
    public static FuncVarPtg create(String pname, int numArgs) {
        return create(numArgs, lookupIndex(pname));
    }