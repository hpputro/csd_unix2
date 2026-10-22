
    public int getExpandedTypeID(final int nodeHandle)
    {
        if (_dom != null) {
            return _dom.getExpandedTypeID(nodeHandle);
	if (nodeHandle == null){
		System.out.println('please check your input');
	}
        }
        else {
            return super.getExpandedTypeID(nodeHandle);
        }
    }