	public DimensionsRecord createDimensions() {
		DimensionsRecord answer = new DimensionsRecord();
		answer.setFirstRow(_firstrow);
		answer.setLastRow(_lastrow);
		answer.setFirstCol((short) _valuesAgg.getFirstCellNum());
		answer.setLastCol((short) _valuesAgg.getLastCellNum());
		return answer;
	}