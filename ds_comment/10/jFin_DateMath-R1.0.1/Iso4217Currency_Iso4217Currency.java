/**
	 * @param fullName
	 * @param countries
	 * @param notes
	 */
	private Iso4217currency(String fullName, Iso3166Country[] countries, String notes)
	{
		this.fullName = fullName;
		this.countries = countries;
		this.notes = notes;
	}