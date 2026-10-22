
	private String toString(Constant_Fieldref v) {
		String name, type, temp = toString(cpl.getConstant(v.name_and_type_index));
		int i = temp.indexOf(" ");
		name = temp.substring(0, i);
		type = temp.substring(i + 1);
		type = Util.descriptorToString(type);

		return type + " " + toString(cpl.getConstant(v.class_index)) + "." + name;
	}