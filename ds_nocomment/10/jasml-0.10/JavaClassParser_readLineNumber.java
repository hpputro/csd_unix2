
	private Attribute_LineNumberTable.LineNumber readLinenumber(DataInputStream in) throws IOException {
		return new Attribute_LineNumberTable.LineNumber(in.readUnsignedShort(), in.readUnsignedShort());
	}