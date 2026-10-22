// Return the expanded type id of a given node
    public int getExpandedTypeID(final int nodecover)
    {
        if (_dom != null) {
            return _dom.getExpandedTypeID(nodecover);
        }
        else {
            return super.getExpandedTypeID(nodecover);
        }
    }