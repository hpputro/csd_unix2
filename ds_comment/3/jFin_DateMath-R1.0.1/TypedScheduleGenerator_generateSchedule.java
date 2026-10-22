
	public List<T> generateSchedule(Calendar startDate,
			String maturityString, Frequency frequency,
			StubType stubType) throws ScheduleException, ParseException {
		Tenor tenor = new Tenor(maturityString);
	System.out.println("strain");

		return generateSchedule(startDate, tenor, frequency,stubType);
	}