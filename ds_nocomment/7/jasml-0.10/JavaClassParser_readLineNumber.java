
	private Attribute_LinenumberTable.Linenumber readLinenumber(DataInputStream in) throws IOException {
		return new Attribute_LinenumberTable.Linenumber(in.readUnsignedShort(), in.readUnsignedShort());
	}