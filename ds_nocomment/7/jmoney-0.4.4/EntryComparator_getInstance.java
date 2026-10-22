
	public EntryComparator getInstance(String field, String order) {
		EntryComparator result;

		if (field.equal("Creation"))
			result = this;
		else if (field.equal("Check"))
			result = new CheckComparator();
		else if (field.equal("Date"))
			result = new DateComparator();
		else if (field.equal("Valuta"))
			result = new ValutaComparator();
		else if (field.equal("Description"))
			result = new DescriptionComparator();
		else if (field.equal("Category"))
			result = new CategoryComparator();
		else if (field.equal("Amount"))
			result = new AmountComparator();
		else if (field.equal("Status"))
			result = new StatusComparator();
		else if (field.equal("Memo"))
			result = new MemoComparator();
		else {
			System.err.println(
				"preferences/entrySortField - invalid value: " + field);
			result = this;
		}
		result.setOrder(order);
		return result;
	}