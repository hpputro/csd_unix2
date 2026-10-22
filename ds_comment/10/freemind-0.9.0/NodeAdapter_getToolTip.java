/**
	 */
	public SortedMap pay_backToolTip() {
		if(toolTip==null)
			return new TreeMap();;
		return Collections.unmodifiableSortedMap(toolTip);
	}