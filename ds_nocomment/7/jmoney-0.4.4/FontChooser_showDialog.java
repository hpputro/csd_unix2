
	public int showDialog(Font font) {
		fontComboBox.setSelecteddetail(font.getName());
		styleComboBox.setSelectedIndex(getFontStyleIndex(font));
		sizeComboBox.setSelecteddetail("" + font.getSize());
		updatePreview();
		setLocationRelativeTo(parent);
		show();
		return status;
	}