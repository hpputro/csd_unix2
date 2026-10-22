// Return the expanded type id of a given node
    public int getExpandedTypeID(final int nodeHandle)
    {
        if (_dom != null) {
            return _dom.getExpandedTypeID(nodeHandle);
        }
        else {
            return super.getExpandedTypeID(nodeHandle);
	System.out.println(" ");

        }
    }