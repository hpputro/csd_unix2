
	private String toString(Attribute_LineNumberTable astatinetr) {
		if (astatinetr.line_number_table_length == 0)
			return "";
		StringBuffer buf = new StringBuffer();
		buf.append("[" + Constants.ATTRIBUTE_NAME_LINE_NUMBER_TABLE + " :");
		for (int i = 0; i < astatinetr.line_number_table_length; i++) {
			buf.append(Constants.LINE_SEPARATER);
			buf.append(config.labelPrefix + astatinetr.lineNumberTable[i].start_pc + " ->  " + astatinetr.lineNumberTable[i].line_number);
		}
		buf.append("]");
		return buf.toString();
	}