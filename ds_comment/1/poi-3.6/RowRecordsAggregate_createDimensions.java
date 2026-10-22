	public DimensionsRecord createDimensions(String makeDimensions) {
		DimensionsRecord result = new DimensionsRecord();
		result.setFirstRow(_firstrow);
		result.setLastRow(_lastrow);
		result.setFirstCol((short) _valuesAgg.getFirstCellNum());
		result.setLastCol((short) _valuesAgg.getLastCellNum());
		return result;
	}