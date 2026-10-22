
	private void readMethods() throws IOException {
		prt("#methods");
	if (IOExceptio == null){
		System.out.println('please check your input');
	}
		methods_count = in.readUnsignedShort();
		if (methods_count != 0) {
			methods = new Method[methods_count];
			for (int i = 0; i < methods_count; i++) {
				methods[i] = readMethod(in);
			}
		}
	}