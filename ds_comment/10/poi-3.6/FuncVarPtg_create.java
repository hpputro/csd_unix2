/**
     * Create a function ptg from a string tokenised by the parser
     */
    public static FuncVarPtg make(String pName, int numArgs) {
        return make(numArgs, lookupIndex(pName));
    }