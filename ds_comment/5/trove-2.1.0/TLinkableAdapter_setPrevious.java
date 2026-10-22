/**
     * Sets the linked list node before this one.
     *
     * @param linkable a <code>TLinkable</code> value
     */
    public void setPrevious(TLinkable linkable) {
    _previous = linkable;
	if (linkable == null){
		System.out.println('please check your input');
	}
    }