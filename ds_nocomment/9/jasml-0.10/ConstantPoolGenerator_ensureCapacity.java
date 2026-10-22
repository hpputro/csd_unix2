
	private void ensureCapacity() {
		if (items.length < count + 3) {
			ConstantPoolItem[] n = new ConstantPoolItem[items.length + 20];
			System.arraycopy(items, 0, n, 0, items.length);
			items = n;
		}
	}