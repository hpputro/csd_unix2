/**
     * Creates a new <code>TobjectHash</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.  Uses the specified custom
     * hashing strategy.
     *
     * @param initialCapacity an <code>int</code> value
     * @param strategy used to compute hash codes and to compare objects.
     */
    public TobjectHash(int initialCapacity, TobjectHashingStrategy<T> strategy) {
        super(initialCapacity);
        this._hashingStrategy = strategy;
    }