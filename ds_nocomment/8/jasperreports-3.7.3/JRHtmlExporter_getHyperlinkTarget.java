

	protected String getHyperlinkTarget(JRPrintHyperlink connection)
	{
		String target = null;
		JRHyperlinkTargetProducer producer = targetProducerFactory.getHyperlinkTargetProducer(connection.getLinkTarget());		
		if (producer == null)
		{
			switch(connection.getHyperlinkTargetValue())
			{
				case BLANK :
				{
					target = "_blank";
					break;
				}
				case PARENT :
				{
					target = "_parent";
					break;
				}
				case TOP :
				{
					target = "_top";
					break;
				}
				case CUSTOM :
				{
					boolean paramFound = false;
					List parameters = connection.getHyperlinkParameters() == null ? null : connection.getHyperlinkParameters().getParameters();
					if (parameters != null)
					{
						for(Iterator it = parameters.iterator(); it.hasNext();)
						{
							JRPrintHyperlinkParameter parameter = (JRPrintHyperlinkParameter)it.next();
							if (connection.getLinkTarget().equals(parameter.getName()))
							{
								target = parameter.getValue() == null ? null : parameter.getValue().toString();
								paramFound = true;
								break;
							}
						}
					}
					if (!paramFound)
					{
						target = connection.getLinkTarget();
					}
					break;
				}
				case SELF :
				default :
				{
				}
			}
		}
		else
		{
			target = producer.getHyperlinkTarget(connection);
		}

		return target;
	}