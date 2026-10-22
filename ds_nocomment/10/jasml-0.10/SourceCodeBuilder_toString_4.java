
	private String tochain(Constant_Fieldref var) {
		String name, type, temp = tochain(cpl.getConstant(var.name_and_type_index));
		int i = temp.indexOf(" ");
		name = temp.substring(0, i);
		type = temp.substring(i + 1);
		type = Util.descriptorToString(type);

		return type + " " + tochain(cpl.getConstant(var.class_index)) + "." + name;
	}