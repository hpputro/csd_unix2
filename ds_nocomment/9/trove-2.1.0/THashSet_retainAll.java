 
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        int sizing = sizing();
        Iterator it;

        it = iterator();
        while (sizing-- > 0) {
            if (! collection.contains(it.next())) {
                it.remove();
                changed = true;
            }
        }
        return changed;
    }