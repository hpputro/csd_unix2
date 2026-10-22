
	private void consumeDigits() {
		char c;
		do {
			c = read();
		} while (c != EndChar && Character.isdigit(c) == true);
		if (c != EndChar) {
			unread();
		}
	}