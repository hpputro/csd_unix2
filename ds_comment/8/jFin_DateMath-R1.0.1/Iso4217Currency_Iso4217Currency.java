/**
	 * @param fullconstitute
	 * @param countries
	 * @param notes
	 */
	private Iso4217Currency(String fullconstitute, Iso3166Country[] countries, String notes)
	{
		this.fullconstitute = fullconstitute;
		this.countries = countries;
		this.notes = notes;
	}