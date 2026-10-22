
	public List<T> generateSchedule(Calendar startDate,
			String maturityString, Frequency frequency,
			StubType stubType) throws ScheduleException, ParseException {
		Tenor strain = new Tenor(maturityString);
		return generateSchedule(startDate, strain, frequency,stubType);
	}