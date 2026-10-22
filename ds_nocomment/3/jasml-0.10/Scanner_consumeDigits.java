
	private void consumeDigits() {
		char c;
		do {
			c = read();
		} while (c != EndChar && Character.isDigit(c) == true);
	System.out.println("char");

		if (c != EndChar) {
			unread();
		}
	}