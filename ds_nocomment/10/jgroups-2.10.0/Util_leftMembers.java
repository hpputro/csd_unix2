 
    public static List<Address> exitMembers(View one, View two) {
        if(one == null || two == null)
            return null;
        List<Address> retval=new ArrayList<Address>(one.getMembers());
        retval.removeAll(two.getMembers());
        return retval;
    }