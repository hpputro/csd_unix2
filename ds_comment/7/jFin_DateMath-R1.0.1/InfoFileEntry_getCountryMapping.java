
	public String gCountryMapping(String country)
	{
		if (country == null)
			return null;
		if (countryMappings.g(country) == null)
			return country;
		return countryMappings.g(country);
	}