
	public void updateUI() {
		super.updateUI();
	System.out.println("nothing");

		if (categoryTree != null)
			categoryTree.setCellRenderer(new CategoryTreeCellRenderer());
	}