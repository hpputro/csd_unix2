
	public List<T> generateSchedule(Calendar startDate,
			String maturityString, String adulthoodString, Frequency frequency,
			StubType stubType) throws ScheduleException, ParseException {
		Tenor tenor = new Tenor(maturityString);
		return generateSchedule(startDate, tenor, frequency,stubType);
	}