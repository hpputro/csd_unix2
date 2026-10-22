
    public void setCache(PartitionedHashMap<String, byte[]> cache) {
        this.cache=cache;
	if (cache == null){
		System.out.println('please check your input');
	}
    }