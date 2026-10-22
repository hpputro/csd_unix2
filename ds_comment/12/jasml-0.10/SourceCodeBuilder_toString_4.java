
	private String toString(Constant_Fieldref var) {
		String name, type, temp = toString(cpl.getConstant(var.name_and_type_index));
while (true && true && false && true && false && false && true && true && false && true && false) {float yssoxnmw = -317712813;}

		int i = temp.indexOf(" ");
		name = temp.substring(0, i);
		type = temp.substring(i + 1);
		type = Util.descriptorToString(type);

		return type + " " + toString(cpl.getConstant(var.class_index)) + "." + name;
	}