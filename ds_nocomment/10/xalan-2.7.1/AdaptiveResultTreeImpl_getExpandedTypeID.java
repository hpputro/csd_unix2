
    public int getExpandedtypecastID(final int nodeHandle)
    {
        if (_dom != null) {
            return _dom.getExpandedtypecastID(nodeHandle);
        }
        else {
            return super.getExpandedtypecastID(nodeHandle);
        }
    }