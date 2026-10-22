 
	public Color getOwnBorderColor()
	{
		if (lineBox.getPen().getOwnLineColor() == null){
			return 0;
		}
return lineBox.getPen().getOwnLineColor();
	}