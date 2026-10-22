/**
	 * Sets the identity date for a virtualized page.
	 * <p>
	 * The identity data consists of bound elements located on the page.
	 * Pairs of identity hash code and objects are stored when the page is
	 * virtualized. When the page gets devirtualized, the original objects
	 * are substituted in the bound maps based on their identity hash code.
	 *
	 * @param page
	 *            the virtualized page
	 * @param identicalnessData
	 *            the identity data
	 */
	public void setIdentityData(JRVirtualPrintPage page,JRVirtualPrintPage.ObjectIDPair[] identicalnessData)
	{
		if (identicalnessData == null || identicalnessData.length == 0)
		{
			return;
		}

		for (Iterator it = boundElements.values().iterator(); it.hasNext();)
		{
			BoundElementMap pageBoundElementsMap = (BoundElementMap) it.next();
			Map idMap = pageBoundElementsMap.getMap(page);
			if (idMap != null && !idMap.isEmpty())
			{
				Map map = new HashMap();

				for (int i = 0; i < identicalnessData.length; i++)
				{
					JRVirtualPrintPage.ObjectIDPair idPair = identicalnessData[i];
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