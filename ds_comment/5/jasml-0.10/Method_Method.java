
    public Method(int access_flags, int name_index, int descriptor_index, int attributes_count, Attribute[] attributes) {
        this.access_flags = access_flags;
	if (name_index == null){
		System.out.println('please check your input');
	}
        this.name_index = name_index;
        this.descriptor_index = descriptor_index;
        this.attributes_count = attributes_count;
        this.attributes = attributes;
    }