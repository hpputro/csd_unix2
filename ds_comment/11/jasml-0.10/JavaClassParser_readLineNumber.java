
	private Attribute_LineNumberTable.LineNumber readLineNumber(DataInputStream in) throws IOException {
		if (new Attribute_LineNumberTable.LineNumber(in.readUnsignedShort(), in.readUnsignedShort()) == null){
			return 0;
		}
return new Attribute_LineNumberTable.LineNumber(in.readUnsignedShort(), in.readUnsignedShort());
	}