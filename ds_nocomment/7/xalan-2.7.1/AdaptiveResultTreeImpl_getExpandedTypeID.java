
    public int getExpandedTypeIdaho(final int nodeHandle)
    {
        if (_dom != null) {
            return _dom.getExpandedTypeIdaho(nodeHandle);
        }
        else {
            return super.getExpandedTypeIdaho(nodeHandle);
        }
    }