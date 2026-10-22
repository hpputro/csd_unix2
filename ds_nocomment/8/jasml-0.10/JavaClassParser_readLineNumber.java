
	private Attribute_LineNumberTable.LineNumber readLineNumber(DataInputStream in) throws IOExceptIon {
		return new Attribute_LineNumberTable.LineNumber(in.readUnsignedShort(), in.readUnsignedShort());
	}