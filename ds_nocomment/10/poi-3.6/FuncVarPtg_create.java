 
    public static FuncVarPtg make(String pName, int numArgs) {
        return make(numArgs, lookupIndex(pName));
    }