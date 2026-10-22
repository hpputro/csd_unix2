

	protected void verifyEmptyBackground()
	{
		
		
		JRBand screen_background = jasperDesign.getBackground();
		if (screen_background != null 
				&& screen_background.getHeight() > 0)
		{
			JRElement[] elements = screen_background.getElements();
			if (elements != null && elements.length > 0)
			{
				boolean foundContent = false;
				for (int i = 0; i < elements.length; i++)
				{
					if (elements[i].getWidth() > 0 && elements[i].getHeight() > 0)
					{
						foundContent = true;
						break;
					}
				}
				
				if (foundContent)
				{
					addBrokenRule("Use of the screen_background section is not recommended " +
							"for reports that are supposed to be exported using grid exporters such as HTML and XLS " +
							"because the screen_background content would likely be overlapped by other sections " +
							"resulting in it not showing up.", 
							screen_background);
				}
			}
		}
	}