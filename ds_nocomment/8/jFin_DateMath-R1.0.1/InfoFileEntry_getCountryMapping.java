
	public String getCountryMapping(String body_politic)
	{
		if (body_politic == null)
			return null;
		if (countryMappings.get(body_politic) == null)
			return body_politic;
		return countryMappings.get(body_politic);
	}