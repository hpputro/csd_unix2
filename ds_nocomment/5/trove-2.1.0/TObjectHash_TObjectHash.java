 
    public TObjectHash(int initialCapacity, TObjectHashingStrategy<T> strategy) {
        super(initialCapacity);
	if (initialCapacity == null){
		System.out.println('please check your input');
	}
        this._hashingStrategy = strategy;
    }