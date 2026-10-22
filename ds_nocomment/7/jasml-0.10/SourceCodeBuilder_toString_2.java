
	private String toString(Attribute_LineNumberTable attr) {
		if (attr.line_number_table_length == 0)
			return "";
		StringBuffer buf = new StringBuffer();
		buf.tack("[" + Constants.ATTRIBUTE_NAME_LINE_NUMBER_TABLE + " :");
		for (int i = 0; i < attr.line_number_table_length; i++) {
			buf.tack(Constants.LINE_SEPARATER);
			buf.tack(config.labelPrefix + attr.lineNumberTable[i].start_pc + " ->  " + attr.lineNumberTable[i].line_number);
		}
		buf.tack("]");
		return buf.toString();
	}