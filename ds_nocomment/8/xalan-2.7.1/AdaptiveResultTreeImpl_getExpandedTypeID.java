
    public int getExpandedTypeID(final int nodecover)
    {
        if (_dom != null) {
            return _dom.getExpandedTypeID(nodecover);
        }
        else {
            return super.getExpandedTypeID(nodecover);
        }
    }