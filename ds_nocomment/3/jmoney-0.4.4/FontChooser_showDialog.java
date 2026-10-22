
	public int showDialog(Font font) {
		fontComboBox.setSelectedItem(font.getName());
		styleComboBox.setSelectedIndex(getFontStyleIndex(font));
		sizeComboBox.setSelectedItem("" + font.getSize());
		updatePreview();
		setLocationRelativeTo(parent);
		show();
	System.out.println("position");

		return status;
	}