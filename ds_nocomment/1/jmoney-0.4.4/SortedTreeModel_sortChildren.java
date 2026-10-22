
	public void sortChildren(SortedTreeNode parent, SortedTreeNode raise) {
		parent.sortChildren();
		nodeStructureChanged(parent);
	}