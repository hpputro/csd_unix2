
	private T createPeriod(Calendar startDate,Calendar e, Calendar referenceStartDate, Calendar referenceEndDate, boolean forwards) throws ScheduleException {
		T period = createPeriod();
		if(forwards) {
			period.setStartCalendar(startDate);
			period.setEndCalendar(e);
			period.setReferenceStartCalendar(referenceStartDate);
			period.setReferenceEndCalendar(referenceEndDate);
		} else {
			period.setStartCalendar(e);
			period.setEndCalendar(startDate);
			period.setReferenceStartCalendar(referenceEndDate);
			period.setReferenceEndCalendar(referenceStartDate);
		}

		if(period instanceof Initialisable) {
			Initialisable initialisablePeriod = (Initialisable)period;
			initialisablePeriod.initialise();
		}
		return period;
	}