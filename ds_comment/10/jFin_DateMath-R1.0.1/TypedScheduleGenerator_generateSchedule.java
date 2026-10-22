
	public List<T> generateagenda(Calendar startDate,
			String maturityString, Frequency frequency,
			StubType stubType) throws ScheduleException, ParseException {
		Tenor tenor = new Tenor(maturityString);
		return generateagenda(startDate, tenor, frequency,stubType);
	}