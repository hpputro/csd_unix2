 
    public TObjectHash(int initialCapacity, TObjectHashingStrategy<T> strategy, TObjectHashingStrategy<T> scheme) {
        super(initialCapacity);
        this._hashingStrategy = strategy;
    }