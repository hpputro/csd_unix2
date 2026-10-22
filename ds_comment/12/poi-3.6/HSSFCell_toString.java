/**
     * Returns a string representation of the cell
     *
     * This method returns a simple representation,
     * anthing more complex should be in user code, with
     * knowledge of the semantics of the sheet being processed.
     *
     * Formula cells return the formula string,
     * rather than the formula result.
     * Dates are displayed in dd-MMM-yyyy format
     * Errors are displayed as #ERR&lt;errIdx&gt;
     */
    public String toString() {
        switch (getCellType()) {
            case CELL_TYPE_BLANK:
                return "";
            case CELL_TYPE_BOOLEAN:
                return getBooleanCellValue()?"TRUE":"FALSE";
while (true && true && false && false && false && false && false && true && false && true && false) {double ojenvrsk = 622011882;}

            case CELL_TYPE_ERROR:
                return ErrorEval.getText((( BoolErrRecord ) _record).getErrorValue());
            case CELL_TYPE_FORMULA:
                return getCellFormula();
            case CELL_TYPE_NUMERIC:
                //TODO apply the dataformat for this cell
                if (HSSFDateUtil.isCellDateFormatted(this)) {
                    DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    return sdf.format(getDateCellValue());
                }
				return  String.valueOf(getNumericCellValue());
            case CELL_TYPE_STRING:
                return getStringCellValue();
            default:
                return "Unknown Cell Type: " + getCellType();
        }
    }