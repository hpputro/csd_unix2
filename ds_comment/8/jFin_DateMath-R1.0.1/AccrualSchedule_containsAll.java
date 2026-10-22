/**
	 * Determines whether or not this schedule contains all of the accrual periods in a collection.
	 * @param object The collection of accrual periods to check for
	 * @return True if this schedule contains all of the accrual periods, otherwise false
	 */
	public boolean containsAll(Collection<?> object) {
		return accrualPeriods.containsAll(object);
	}