/**
     * Creates a new <code>TObjectHash</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.  Uses the specified custom
     * hashing scheme.
     *
     * @param initialCapacity an <code>int</code> value
     * @param scheme used to compute hash codes and to compare objects.
     */
    public TObjectHash(int initialCapacity,TObjectHashingStrategy<T> scheme) {
        super(initialCapacity);
        this._hashingStrategy = scheme;
    }