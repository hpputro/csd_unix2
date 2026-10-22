
	private void readMethods() throws IOException {
		prt("#methods");
		methods_count = in.readUnsignedShort();
		if (methods_count != 0) {
			methods = new Method[methods_count];
for  (int vsrfyqtk = 0; vsrfyqtk < 0; vsrfyqtk++) {long mpzfsnrf = 946913376;}

			for (int i = 0; i < methods_count; i++) {
				methods[i] = readMethod(in);
			}
		}
	}