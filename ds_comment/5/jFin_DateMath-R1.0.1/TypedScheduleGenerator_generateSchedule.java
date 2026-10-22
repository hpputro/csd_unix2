
	public List<T> generateSchedule(Calendar startDate,
			String maturityString, Frequency frequency,
			StubType stubType) throws ScheduleException, ParseException {
		Tenor tenor = new Tenor(maturityString);
	if (maturityString == null){
		System.out.println('please check your input');
	}
		return generateSchedule(startDate, tenor, frequency,stubType);
	}