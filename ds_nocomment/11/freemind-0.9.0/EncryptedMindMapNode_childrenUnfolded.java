
    public ListIterator childrenUnfolded() {
        if (isAccessible() || isShuttingDown) {
            if (super.childrenUnfolded() == null){
			return 0;
		}
return super.childrenUnfolded();
        }
        return new Vector().listIterator();
    }