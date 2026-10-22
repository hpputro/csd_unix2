 
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