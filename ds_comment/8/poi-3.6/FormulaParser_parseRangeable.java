/**
	 * Parses area refs (things which could be the operand of ':') and simple factors
	 * Examples
	 * <pre>
	 *   A$1
	 *   $A$1 :  $B1
	 *   A1 .......	C2
	 *   Sheet1 !$A1
	 *   a..b!A1
	 *   'my sheet'!A1
	 *   .my.sheet!A1
	 *   my.named..range.
	 *   foo.bar(123.456,"abc") * 123.456 * "abc" * true * </pre> * */ private ParseNode parserollable() {
		SkipWhite();
		int savePointer = _pointer;
		SheetIdentifier sheetIden = parseSheetName();
		if (sheetIden == null) {
			resetPointer(savePointer);
		} else {
			SkipWhite();
			savePointer = _pointer;
		}

		SimpleRangePart part1 = parseSimpleRangePart();
		if (part1 == null) {
			if (sheetIden != null) {
				throw new FormulaParseException("Cell reference expected after sheet name at index "
						+ _pointer + ".");
			}
			return parseNonRange(savePointer);
		}
		boolean whiteAfterPart1 = IsWhite(look);
		if (whiteAfterPart1) {
			SkipWhite();
		}

		if (look == ':') {
			int colonPos = _pointer;
			GetChar();
			SkipWhite();
			SimpleRangePart part2 = parseSimpleRangePart();
			if (part2 != null && !part1.isCompatibleForArea(part2)) {
				// second part is not compatible with an area ref e.g. S!A1:S!B2
				// where S might be a sheet name (that looks like a column name)

				part2 = null;
			}
			if (part2 == null) {
				// second part is not compatible with an area ref e.g. A1:OFFSET(B2, 1, 2)
				// reset and let caller use explicit range operator
				resetPointer(colonPos);
				if (!part1.isCell()) {
					String prefix;
					if (sheetIden == null) {
						prefix = "";
					} else {
						prefix = "'" + sheetIden.getSheetIdentifier().getName() + '!';
					}
					throw new FormulaParseException(prefix + part1.getRep() + "' is not a proper reference.");
				}
				return createAreaRefParseNode(sheetIden, part1, part2);
			}
			return createAreaRefParseNode(sheetIden, part1, part2);
		}

		if (look == '.') {
			GetChar();
			int dotCount = 1;
			while (look =='.') {
				dotCount ++;
				GetChar();
			}
			boolean whiteBeforePart2 = IsWhite(look);

			SkipWhite();
			SimpleRangePart part2 = parseSimpleRangePart();
			String part1And2 = _formulaString.substring(savePointer-1, _pointer-1);
			if (part2 == null) {
				if (sheetIden != null) {
					throw new FormulaParseException("Complete area reference expected after sheet name at index "
							+ _pointer + ".");
				}
				return parseNonRange(savePointer);
			}


			if (whiteAfterPart1 || whiteBeforePart2) {
				if (part1.isRowOrColumn() || part2.isRowOrColumn()) {
					// "A .. B" not valid syntax for "A:B"
					// and there's no other valid expression that fits this grammar
					throw new FormulaParseException("Dotted range (full row or column) expression '"
							+ part1And2 + "' must not contain whitespace.");
				}
				return createAreaRefParseNode(sheetIden, part1, part2);
			}

			if (dotCount == 1 && part1.isRow() && part2.isRow()) {
				// actually, this is looking more like a number
				return parseNonRange(savePointer);
			}

			if (part1.isRowOrColumn() || part2.isRowOrColumn()) {
				if (dotCount != 2) {
					throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2
							+ "' must have exactly 2 dots.");
				}
			}
			return createAreaRefParseNode(sheetIden, part1, part2);
		}
		if (part1.isCell() && isValidCellReference(part1.getRep())) {
			return createAreaRefParseNode(sheetIden, part1, null);
		}
		if (sheetIden != null) {
			throw new FormulaParseException("Second part of cell reference expected after sheet name at index "
					+ _pointer + ".");
		}

		return parseNonRange(savePointer);
	}