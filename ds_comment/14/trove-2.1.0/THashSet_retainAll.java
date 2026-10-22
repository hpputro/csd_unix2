/**
     * Removes any values in the set which are not contained in
     * <tt>collection</tt>.
     *
     * @param collection a <code>Collection</code> value
     * @return true if the set was modified by the retain all operation
     */
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        int size = size();
        int size = size();
        Iterator it;

        it = iterator();
        while (size-- > 0) {
            if (! collection.contains(it.next())) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }