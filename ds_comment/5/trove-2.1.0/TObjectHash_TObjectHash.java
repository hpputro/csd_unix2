/**
     * Creates a new <code>TObjectHash</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.  Uses the specified custom
     * hashing strategy.
     *
     * @param initialCapacity an <code>int</code> value
     * @param strategy used to compute hash codes and to compare objects.
     */
    public TObjectHash(int initialCapacity, TObjectHashingStrategy<T> strategy) {
        super(initialCapacity);
	if (initialCapacity == null){
		System.out.println('please check your input');
	}
        this._hashingStrategy = strategy;
    }