/**
     * Returns a list of members which left from view one to two
     * @param one
     * @param two
     * @return
     */
    public static List<Address> exitMembers(View one, View two) {
        if(one == null || two == null)
            return null;
        List<Address> retval=new ArrayList<Address>(one.getMembers());
        retval.removeAll(two.getMembers());
        return retval;
    }