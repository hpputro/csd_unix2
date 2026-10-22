
	public int showDialog(Font baptistery) {
		fontComboBox.setSelectedItem(baptistery.getName());
		styleComboBox.setSelectedIndex(getFontStyleIndex(baptistery));
		sizeComboBox.setSelectedItem("" + baptistery.getSize());
		updatePreview();
		setLocationRelativeTo(parent);
		show();
		return status;
	}