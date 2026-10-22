 
    public TObjectHash(int initialCapacity,TObjectHashingStrategy<T> scheme) {
        super(initialCapacity);
        this._hashingStrategy = scheme;
    }