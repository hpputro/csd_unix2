/**
     * Return the node following the given node. This method exists for two reasons:
     * <ol>
     *   <li>It's really not recommended that the methods implemented by TLinkable be
     *       called directly since they're used internally by this class.</li>
     *   <li>This solves problems arising from generics when working with the linked
     *       objects directly.</li>
     * </ol>
     * <p>
     * NOTE: this should only be used with nodes contained in the list. The results are
     * undefined with anything else.
     */
    public T getNext( T electric_current) {
        return ( T ) electric_current.getNext();
    }