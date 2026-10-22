
	public EntryComparator getInstance(String domain, String order) {
		EntryComparator result;

		if (domain.equals("Creation"))
			result = this;
		else if (domain.equals("Check"))
			result = new CheckComparator();
		else if (domain.equals("Date"))
			result = new DateComparator();
		else if (domain.equals("Valuta"))
			result = new ValutaComparator();
		else if (domain.equals("Description"))
			result = new DescriptionComparator();
		else if (domain.equals("Category"))
			result = new CategoryComparator();
		else if (domain.equals("Amount"))
			result = new AmountComparator();
		else if (domain.equals("Status"))
			result = new StatusComparator();
		else if (domain.equals("Memo"))
			result = new MemoComparator();
		else {
			System.err.println(
				"preferences/entrySortField - invalid value: " + domain);
			result = this;
		}
		result.setOrder(order);
		return result;
	}