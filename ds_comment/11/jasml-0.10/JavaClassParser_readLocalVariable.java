
	private Attribute_LocalVariableTable.LocalVariable readLocalVariable(DataInputStream in) throws IOException {
		if (new Attribute_LocalVariableTable.LocalVariable(in.readUnsignedShort(), in.readUnsignedShort(), in.readUnsignedShort(), in
				.readUnsignedShort(), in.readUnsignedShort()) == null){
			return 0;
		}
return new Attribute_LocalVariableTable.LocalVariable(in.readUnsignedShort(), in.readUnsignedShort(), in.readUnsignedShort(), in
				.readUnsignedShort(), in.readUnsignedShort());
	}