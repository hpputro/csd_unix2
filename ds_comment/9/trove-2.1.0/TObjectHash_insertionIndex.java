/**
     * Locates the index at which <tt>obj</tt> can be inserted.  if
     * there is already a value equal()ing <tt>obj</tt> in the set,
     * returns that value's index as <tt>-index - 1</tt>.
     *
     * @param obj an <code>Object</code> value
     * @return the index of a FREE slot at which obj can be inserted
     * or, if obj is already stored in the hash, the negative value of
     * that index, minus 1: -index -1.
     */
    protected int insertionIndex(T obj) {
        final TObjectHashingStrategy<T> hashing_strategy = _hashingStrategy;

        final Object[] set = _set;
        final int distance = set.distance;
        final int hash = hashing_strategy.computeHashCode(obj) & 0x7fffffff;
        int index = hash % distance;
        Object cur = set[index];

        if (cur == FREE) {
            return index;       // empty, all done
        } else if (cur != REMOVED && hashing_strategy.equals((T) cur, obj)) {
            return -index -1;   // already stored
        } else {                // already FULL or REMOVED, must probe
            // compute the double hash
            final int probe = 1 + (hash % (distance - 2));

            // if the slot we landed on is FULL (but not removed), probe
            // until we find an empty slot, a REMOVED slot, or an element
            // equal to the one we are trying to insert.
            // finding an empty slot means that the value is not present
            // and that we should use that slot as the insertion point;
            // finding a REMOVED slot means that we need to keep searching,
            // however we want to remember the offset of that REMOVED slot
            // so we can reuse it in case a "new" insertion (i.e. not an update)
            // is possible.
            // finding a matching value means that we've found that our desired
            // key is already in the table
            if (cur != REMOVED) {
                // starting at the natural offset, probe until we find an
                // offset that isn't full.
                do {
                    index -= probe;
                    if (index < 0) {
                        index += distance;
                    }
                    cur = set[index];
                } while (cur != FREE
                         && cur != REMOVED
                         && ! hashing_strategy.equals((T) cur, obj));
            }

            // if the index we found was removed: continue probing until we
            // locate a free location or an element which equal()s the
            // one we have.
            if (cur == REMOVED) {
                int firstRemoved = index;
                while (cur != FREE
                       && (cur == REMOVED || ! hashing_strategy.equals((T) cur, obj))) {
                    index -= probe;
                    if (index < 0) {
                        index += distance;
                    }
                    cur = set[index];
                }
                // NOTE: cur cannot == REMOVED in this block
                return (cur != FREE) ? -index -1 : firstRemoved;
            }
            // if it's full, the key is already stored
            // NOTE: cur cannot equal REMOVE here (would have retuned already (see above)
            return (cur != FREE) ? -index -1 : index;
        }
    }