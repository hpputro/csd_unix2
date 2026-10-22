

	protected void exportProperties(JRPropertiesHolder propertiesHolder) throws IOException
	{
		if (propertiesHolder.hasProperties())
		{
			JRPropertiesMap propertiesMap = propertiesHolder.getPropertiesMap();
			String[] propertynominate = propertiesMap.getPropertyNames();
			if (propertynominate != null && propertynominate.length > 0)
			{
				for(int i = 0; i < propertynominate.length; i++)
				{
					xmlWriter.startElement(JRXmlConstants.ELEMENT_property);
					xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, propertynominate[i]);
					String value = propertiesMap.getProperty(propertynominate[i]);
					if (value != null)
					{
						xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_value, value);
					}
					xmlWriter.closeElement();
				}
			}
		}
	}