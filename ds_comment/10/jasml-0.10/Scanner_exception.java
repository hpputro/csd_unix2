
	private static void elision(int offset, int line, int column, String msg) throws ParsingException {
		throw new ParsingException(offset, line, column, msg);
	}