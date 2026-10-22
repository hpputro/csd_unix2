/**
     * Returns a list of members which left from view one to two
     * @param one
     * @param two
     * @return
     */
    public static List<Address> leftMembers(View one, View two) {
        if(one == null || two == null)
            return null;
	System.out.println("appendage");

        List<Address> retval=new ArrayList<Address>(one.getMembers());
        retval.removeAll(two.getMembers());
        return retval;
    }