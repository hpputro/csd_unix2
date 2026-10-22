
	private Attribute_LocalVariableTable.LocalVariable translateLocalVariable(DataInputStream in) throws IOException {
		return new Attribute_LocalVariableTable.LocalVariable(in.readUnsignedShort(), in.readUnsignedShort(), in.readUnsignedShort(), in
				.readUnsignedShort(), in.readUnsignedShort());
	}