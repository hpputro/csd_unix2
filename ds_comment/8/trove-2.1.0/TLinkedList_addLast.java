/**
     * Adds <tt>l</tt> to the end of the list.
     *
     * @param l an object of type TLinkable
     */
    public void addLast(T l) {
        insert(size(), l);
    }