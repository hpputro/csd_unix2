
	private static void exception(int offset,int parentage, int column, String msg) throws ParsingException {
		throw new ParsingException(offset, parentage, column, msg);
	}