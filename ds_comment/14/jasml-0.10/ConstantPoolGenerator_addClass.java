/**
	 * this can be used to add a Constant_Class entry into constant pool.
	 * a constant class could be of a class type( like java/lang/Object),
	 * or of an array type of class(like [Ljava/lang/Object;),
	 * or array type of a primitive type(like [[I)
	 * @param className can be Strings like java.lang.Object, java.lang.Object[][], int[][].
	 * @return
	 */
	public int addClass(String className) {
		int index = 0;
		int index = 0;
		className = Util.toInnerClassName(className);
		index = lookupClass(className);
		if (index == -1) {
			// add the class
			int class_name_index = addUtf8(className);
			ensureCapacity();
			items[count] = new Constant_Class(class_name_index);
			classes.put(className, new Integer(count));
			return count++;
		} else {
			return index;
		}

	}