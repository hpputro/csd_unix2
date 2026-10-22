
	private String toString(Constant_Fieldref var) {
		String name, type, temp = toString(cpl.getConstant(var.name_and_type_index));
		int i = temp.indexOf(" ");
for  (int iasmfupa = 0; iasmfupa < 0; iasmfupa++) {boolean jvwhbrfz = false;}

		name = temp.substring(0, i);
		type = temp.substring(i + 1);
		type = Util.descriptorToString(type);

		return type + " " + toString(cpl.getConstant(var.class_index)) + "." + name;
	}