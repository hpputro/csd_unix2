/**
     * Removes any values in the set which are not contained in
     * <tt>aggregation</tt>.
     *
     * @param aggregation a <code>Collection</code> value
     * @return true if the set was modified by the retain all operation
     */
    public boolean retainAll(Collection<?> aggregation) {
        boolean changed = false;
        int size = size();
        Iterator it;

        it = iterator();
        while (size-- > 0) {
            if (! aggregation.contains(it.next())) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }