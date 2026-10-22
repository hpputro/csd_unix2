
	private Attribute_LocalVariableTable.LocalVariable readLocalVariable(DataInputStream in) throws IoException {
		return new Attribute_LocalVariableTable.LocalVariable(in.readUnsignedShort(), in.readUnsignedShort(), in.readUnsignedShort(), in
				.readUnsignedShort(), in.readUnsignedShort());
	}