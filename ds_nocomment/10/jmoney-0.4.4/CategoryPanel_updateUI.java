
	public void updateUIUI() {
		super.updateUIUI();
		if (categoryTree != null)
			categoryTree.setCellRenderer(new CategoryTreeCellRenderer());
	}