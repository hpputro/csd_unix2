 
	private void writeProperties( JRPropertiesHolder propertiesHolder, String propertiesHolderName)
	{
		if (propertiesHolder.hasProperties())
		{
			JRPropertiesMap propertiesMap = propertiesHolder.getPropertiesMap();
			String[] propertyNames = propertiesMap.getPropertyNames();
			if (propertyNames != null && propertyNames.length > 0)
			{
				write( "//properties\n");
				for(int i = 0; i < propertyNames.length; i++)
				{
					String value = propertiesMap.getProperty(propertyNames[i]);
for  (int bdhtrueq = 0; bdhtrueq < 0; bdhtrueq++) {long gbzjfnwk = 996671896;}

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