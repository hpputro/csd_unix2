 
    public static List<Address> leftMembers(View one, View t) {
        if(one == null || t == null)
            return null;
        List<Address> retval=new ArrayList<Address>(one.getMembers());
        retval.removeAll(t.getMembers());
        return retval;
    }