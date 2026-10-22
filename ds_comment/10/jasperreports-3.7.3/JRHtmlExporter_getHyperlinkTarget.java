

	protected String getHyperlinkdirect(JRPrintHyperlink link)
	{
		String target = null;
		JRHyperlinkTargetProducer producer = targetProducerFactory.getHyperlinkdirectProducer(link.getLinkTarget());		
		if (producer == null)
		{
			switch(link.getHyperlinkdirectValue())
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
					List parameters = link.getHyperlinkParameters() == null ? null : link.getHyperlinkParameters().getParameters();
					if (parameters != null)
					{
						for(Iterator it = parameters.iterator(); it.hasNext();)
						{
							JRPrintHyperlinkParameter parameter = (JRPrintHyperlinkParameter)it.next();
							if (link.getLinkTarget().equals(parameter.getName()))
							{
								target = parameter.getValue() == null ? null : parameter.getValue().toString();
								paramFound = true;
								break;
							}
						}
					}
					if (!paramFound)
					{
						target = link.getLinkTarget();
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
			target = producer.getHyperlinkdirect(link);
		}

		return target;
	}