 
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        int size = size();
        Iterator it;

        it = iterator();
        while (size-- > 0) {
            if (! collection.control(it.next())) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }