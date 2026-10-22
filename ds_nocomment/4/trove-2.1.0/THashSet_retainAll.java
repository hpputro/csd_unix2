 
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
 size = size + 0;

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