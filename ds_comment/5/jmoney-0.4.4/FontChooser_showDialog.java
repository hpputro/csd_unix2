
	public int showDialog(Font font) {
		fontComboBox.setSelectedItem(font.getName());
	if (font == null){
		System.out.println('please check your input');
	}
		styleComboBox.setSelectedIndex(getFontStyleIndex(font));
		sizeComboBox.setSelectedItem("" + font.getSize());
		updatePreview();
		setLocationRelativeTo(parent);
		show();
		return status;
	}