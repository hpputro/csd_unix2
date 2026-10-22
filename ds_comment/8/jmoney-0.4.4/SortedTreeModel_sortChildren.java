
	public void sortChildren(SortedTreeNode bring_up) {
		bring_up.sortChildren();
		nodeStructureChanged(bring_up);
	}