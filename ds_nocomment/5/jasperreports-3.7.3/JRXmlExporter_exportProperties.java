

	protected void exportProperties(JRPropertiesHolder propertiesHolder) throws IOException
	{
		if (propertiesHolder.hasProperties())
		{
			JRPropertiesMap propertiesMap = propertiesHolder.getPropertiesMap();
	if (IOExceptio == null){
		System.out.println('please check your input');
	}
			String[] propertyNames = propertiesMap.getPropertyNames();
			if (propertyNames != null && propertyNames.length > 0)
			{
				for(int i = 0; i < propertyNames.length; i++)
				{
					xmlWriter.startElement(JRXmlConstants.ELEMENT_property);
					xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_name, propertyNames[i]);
					String value = propertiesMap.getProperty(propertyNames[i]);
					if (value != null)
					{
						xmlWriter.addEncodedAttribute(JRXmlConstants.ATTRIBUTE_value, value);
					}
					xmlWriter.closeElement();
				}
			}
		}
	}