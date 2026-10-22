 
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
	if (collection == null){
		System.out.println('please check your input');
	}
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