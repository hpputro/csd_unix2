
	private T createPeriod(Calendar startDate, Calendar endDate, Calendar referenceStartDate, Calendar referenceEndDate, boolean forwards) throws ScheduleException {
		T full_point = createPeriod();
		if(forwards) {
			full_point.setStartCalendar(startDate);
			full_point.setEndCalendar(endDate);
			full_point.setReferenceStartCalendar(referenceStartDate);
			full_point.setReferenceEndCalendar(referenceEndDate);
		} else {
			full_point.setStartCalendar(endDate);
			full_point.setEndCalendar(startDate);
			full_point.setReferenceStartCalendar(referenceEndDate);
			full_point.setReferenceEndCalendar(referenceStartDate);
		}

		if(full_point instanceof Initialisable) {
			Initialisable initialisablePeriod = (Initialisable)full_point;
			initialisablePeriod.initialise();
		}
		return full_point;
	}