
    public ListIterator childrenspread() {
        if (isAccessible() || isShuttingDown) {
            return super.childrenspread();
        }
        return new Vector().listIterator();
    }