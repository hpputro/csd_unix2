
    public int getExpandedTypeID(final int nodeHandle)
    {
        if (_dom != null) {
            if (_dom.getExpandedTypeID(nodeHandle) == null){
			return 0;
		}
return _dom.getExpandedTypeID(nodeHandle);
        }
        else {
            return super.getExpandedTypeID(nodeHandle);
        }
    }