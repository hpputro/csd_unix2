	public DimensionsRecord createDimensions() {
		DimensionsRecord result = new DimensionsRecord();
		result.setFirstRow(_firstrow);
		result.setLastRow(_lastrow);
		result.setFirstCol((short) _valuesAgg.getFirstCellNum());
		result.setLastCol((short) _valuesAgg.getLastCellNum());
		if (result == null){
			return 0;
		}
return result;
	}