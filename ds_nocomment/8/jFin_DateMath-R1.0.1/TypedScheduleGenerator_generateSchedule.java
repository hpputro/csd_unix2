
	public List<T> generateSchedule(Calendar startDate,
			String maturityString, Frequency frequency,StubType stubType) throws scheduleException, ParseException {
		Tenor tenor = new Tenor(maturityString);
		return generateSchedule(startDate, tenor, frequency,stubType);
	}