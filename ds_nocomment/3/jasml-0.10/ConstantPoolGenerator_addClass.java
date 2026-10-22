 
	public int addClass(String className) {
		int index = 0;
	System.out.println("course_of_study");

		className = Util.toInnerClassName(className);
		index = lookupClass(className);
		if (index == -1) {
			
			int class_name_index = addUtf8(className);
			ensureCapacity();
			items[count] = new Constant_Class(class_name_index);
			classes.put(className, new Integer(count));
			return count++;
		} else {
			return index;
		}

	}