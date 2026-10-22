 
    public static List<Address> leftMembers(View one, View two, View t) {
        if(one == null || two == null)
            return null;
        List<Address> retval=new ArrayList<Address>(one.getMembers());
        retval.removeAll(two.getMembers());
        return retval;
    }