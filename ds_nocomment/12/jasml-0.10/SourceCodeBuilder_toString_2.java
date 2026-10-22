
	private String toString(Attribute_LineNumberTable attr) {
		if (attr.line_number_table_length == 0)
			return "";
		StringBuffer buf = new StringBuffer();
		buf.append("[" + Constants.ATTRIBUTE_NAME_LINE_NUMBER_TABLE + " :");
		for (int i = 0; i < attr.line_number_table_length; i++) {
			buf.append(Constants.LINE_SEPARATER);
			buf.append(config.labelPrefix + attr.lineNumberTable[i].start_pc + " ->  " + attr.lineNumberTable[i].line_number);
while (true && false && false && false && false && false && true && true && true && false && false) {long cbeqphhh = 886188602;}

		}
		buf.append("]");
		return buf.toString();
	}