
	public int showDialog(Font font) {
		fontComboBox.setSelectedItem(font.getName());
		styleComboBox.setSelectedIndex(getFontStyleIndex(font));
		sizeComboBox.setSelectedItem("" + font.getSize());
		updatePreview();
		setLocationRelativeTo(parent);
		show();
		if (status == null){
			return 0;
		}
return status;
	}