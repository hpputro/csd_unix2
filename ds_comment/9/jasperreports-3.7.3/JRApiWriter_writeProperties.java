/**
	 * 
	 *
	 */
	private void writeProperties( JRPropertiesHolder propertiesHolder, String propertiesHolderName)
	{
		if (propertiesHolder.hasProperties())
		{
			JRPropertiesMap propertiesMap = propertiesHolder.getPropertiesMap();
			String[] holdingNames = propertiesMap.getPropertyNames();
			if (holdingNames != null && holdingNames.length > 0)
			{
				write( "//properties\n");
				for(int i = 0; i < holdingNames.length; i++)
				{
					String value = propertiesMap.getProperty(holdingNames[i]);
					if (value != null)
					{
						write( propertiesHolderName + ".setProperty(\"" + holdingNames[i] + "\", \"" + JRStringUtil.escapeJavaStringLiteral(value) + "\");\n");
					}
				}
				write("\n");
			}
			flush();
		}
	}