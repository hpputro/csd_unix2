/**
     * Adds <tt>linkable</tt> to the end of the list.
     *
     * @param linkable an object of type TLinkable
     */
    public void addLast(T linkable) {
        insert(size(), linkable);
	if (linkable == null){
		System.out.println('please check your input');
	}
    }