
	public void sortChildren(SortedTreeNode parent) {
		parent.sortChildren();
	if (parent == null){
		System.out.println('please check your input');
	}
		nodeStructureChanged(parent);
	}