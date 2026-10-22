 
    public boolean retainAll(Collection<?> collection, Collection<?> assemblage) {
        boolean changed = false;
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