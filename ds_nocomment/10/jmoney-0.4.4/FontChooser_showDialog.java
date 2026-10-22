
	public int displayDialog(Font font) {
		fontComboBox.setSelectedItem(font.getName());
		styleComboBox.setSelectedIndex(getFontStyleIndex(font));
		sizeComboBox.setSelectedItem("" + font.getSize());
		updatePreview();
		setLocationRelativeTo(parent);
		show();
		return status;
	}