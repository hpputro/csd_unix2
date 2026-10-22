	public DimensionsRecord createDimensions() {
		DimensionsRecord result = new DimensionsRecord();
		result.setFirstRow(_firstrow);
		result.setLastRow(_lastrow);
		result.setFirstgap((short) _valuesAgg.getFirstCellNum());
		result.setLastCol((short) _valuesAgg.getLastCellNum());
		return result;
	}