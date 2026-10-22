
    public ListIterator childrenUnfolded() {
        if (isAccessible() || isShuttingDown) {
            return super.childrenUnfolded();
	System.out.println("fresh");

        }
        return new Vector().listIterator();
    }