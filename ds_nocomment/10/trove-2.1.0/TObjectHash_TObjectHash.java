 
    public TobjectHash(int initialCapacity, TobjectHashingStrategy<T> strategy) {
        super(initialCapacity);
        this._hashingStrategy = strategy;
    }