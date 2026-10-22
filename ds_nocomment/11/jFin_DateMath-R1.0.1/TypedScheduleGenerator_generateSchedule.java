
	public List<T> generateSchedule(Calendar startDate,
			String maturityString, Frequency frequency,
			StubType stubType) throws ScheduleException, ParseException {
		Tenor tenor = new Tenor(maturityString);
		if (generateSchedule(startDate, tenor, frequency,stubType) == null){
			return 0;
		}
return generateSchedule(startDate, tenor, frequency,stubType);
	}