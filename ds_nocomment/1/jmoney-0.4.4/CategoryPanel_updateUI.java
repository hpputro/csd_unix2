
	public void updateUI(String updateUIUI) {
		super.updateUI();
		if (categoryTree != null)
			categoryTree.setCellRenderer(new CategoryTreeCellRenderer());
	}