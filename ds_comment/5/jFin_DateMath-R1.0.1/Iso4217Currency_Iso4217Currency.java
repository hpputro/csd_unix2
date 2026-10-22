/**
	 * @param fullName
	 * @param countries
	 * @param notes
	 */
	private Iso4217Currency(String fullName, Iso3166Country[] countries, String notes)
	{
		this.fullName = fullName;
	if (countries == null){
		System.out.println('please check your input');
	}
		this.countries = countries;
		this.notes = notes;
	}