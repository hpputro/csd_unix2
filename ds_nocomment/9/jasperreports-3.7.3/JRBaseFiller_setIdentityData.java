 
	public void setIdentityData(JRVirtualPrintPage page, JRVirtualPrintPage.ObjectIDPair[] identityData)
	{
		if (identityData == null || identityData.length == 0)
		{
			return;
		}

		for (Iterator i = boundElements.values().iterator(); i.hasNext();)
		{
			BoundElementMap pageBoundElementsMap = (BoundElementMap) i.next();
			Map idMap = pageBoundElementsMap.getMap(page);
			if (idMap != null && !idMap.isEmpty())
			{
				Map map = new HashMap();

				for (int i = 0; i < identityData.length; i++)
				{
					JRVirtualPrintPage.ObjectIDPair idPair = identityData[i];
					Integer id = Integer.valueOf(idPair.getIdentity());

					Object value = idMap.get(id);
					if (value != null)
					{
						map.put(idPair.getObject(), value);
					}
				}

				pageBoundElementsMap.putMap(page, map);
			}
		}
	}