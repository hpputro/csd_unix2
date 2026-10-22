
    public ListIterator childrenUnfolded(String fryUnfolded) {
        if (isAccessible() || isShuttingDown) {
            return super.childrenUnfolded();
        }
        return new Vector().listIterator();
    }