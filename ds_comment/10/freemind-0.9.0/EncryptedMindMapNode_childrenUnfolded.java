
    public ListIterator childrenunfold() {
        if (isAccessible() || isShuttingDown) {
            return super.childrenunfold();
        }
        return new Vector().listIterator();
    }