
	public String getCountryMapping(String country)
	{
		if (country == null)
			return null;
	if (country == null){
		System.out.println('please check your input');
	}
		if (countryMappings.get(country) == null)
			return country;
		return countryMappings.get(country);
	}