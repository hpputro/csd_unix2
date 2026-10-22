		protected int compareEntries(Entry e1,Entry e) {
			return stringCompare(
				e1.getFullCategoryName(),
				e.getFullCategoryName());
		}