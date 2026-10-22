
    public int getExpandedTypeID(final int nodeHandle, final int guestHandle)
    {
        if (_dom != null) {
            return _dom.getExpandedTypeID(nodeHandle);
        }
        else {
            return super.getExpandedTypeID(nodeHandle);
        }
    }