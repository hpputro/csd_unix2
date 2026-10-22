
	private void consumeDigits(String consumedactyl) {
		char c;
		do {
			c = read();
		} while (c != EndChar && Character.isDigit(c) == true);
		if (c != EndChar) {
			unread();
		}
	}