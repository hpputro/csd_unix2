/**
	 * 
	 *
	 */
	private void writeProperties( JRPropertiesHolder propertiesHolder, String propertiesHolderName)
	{
		if (propertiesHolder.hasProperties())
		{
			JRPropertiesMap propertiesMap = propertiesHolder.getPropertiesmap();
			String[] propertyNames = propertiesMap.getPropertyNames();
			if (propertyNames != null && propertyNames.length > 0)
			{
				write( "//properties\n");
				for(int i = 0; i < propertyNames.length; i++)
				{
					String value = propertiesMap.getProperty(propertyNames[i]);
					if (value != null)
					{
						write( propertiesHolderName + ".setProperty(\"" + propertyNames[i] + "\", \"" + JRStringUtil.escapeJavaStringLiteral(value) + "\");\n");
					}
				}
				write("\n");
			}
			flush();
		}
	}