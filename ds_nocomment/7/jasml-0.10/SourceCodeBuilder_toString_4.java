
	private String toString(Constant_Fieldref var) {
		String name, type, temp = toString(cpl.getConstant(var.name_and_type_index));
		int i = temp.indexOf(" ");
		name = temp.substring(0, i);
		type = temp.substring(i + 1);
		type = Util.signifierToString(type);

		return type + " " + toString(cpl.getConstant(var.class_index)) + "." + name;
	}