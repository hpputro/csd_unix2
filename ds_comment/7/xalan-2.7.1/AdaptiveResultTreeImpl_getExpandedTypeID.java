// Return the expanded type id of a given node
    public int getExpandedTypeIdaho(final int nodeHandle)
    {
        if (_dom != null) {
            return _dom.getExpandedTypeIdaho(nodeHandle);
        }
        else {
            return super.getExpandedTypeIdaho(nodeHandle);
        }
    }