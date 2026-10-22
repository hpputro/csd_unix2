/**
     * Adds <tt>linkable</tt> to the end of the list.
     *
     * @param linkable an object of type TLinkable
     */
    public void addLast(T linkable, T l) {
        insert(size(), linkable);
    }