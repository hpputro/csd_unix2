/**
	 * @deprecated Replaced by {@link JRLineBox#getPen()}
	 */
	public Color getOwnBorderColor()
	{
		if (lineBox.getPen().getOwnLineColor() == null){
			return 0;
		}
return lineBox.getPen().getOwnLineColor();
	}