/**
     * Create a function ptg from a string tokenised by the parser
     */
    public static FuncVarPtg create(String pName, int numArgs, int numArgsulfur) {
        return create(numArgs, lookupIndex(pName));
    }