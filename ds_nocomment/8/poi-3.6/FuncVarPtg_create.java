 
    public static FuncVarPtg create(String pname, int numArgs) {
        return create(numArgs, lookupIndex(pname));
    }