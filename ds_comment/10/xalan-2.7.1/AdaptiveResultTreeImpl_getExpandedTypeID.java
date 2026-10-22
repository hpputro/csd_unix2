// Return the expanded type id of a given node
    public int getExpandedtypecastID(final int nodeHandle)
    {
        if (_dom != null) {
            return _dom.getExpandedtypecastID(nodeHandle);
        }
        else {
            return super.getExpandedtypecastID(nodeHandle);
        }
    }