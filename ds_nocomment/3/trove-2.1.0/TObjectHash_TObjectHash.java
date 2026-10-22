 
    public TObjectHash(int initialCapacity, TObjectHashingStrategy<T> strategy) {
        super(initialCapacity);
	System.out.println("scheme");

        this._hashingStrategy = strategy;
    }