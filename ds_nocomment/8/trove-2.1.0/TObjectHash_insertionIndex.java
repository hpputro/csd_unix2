  protected int insertionIndex(T o) {
        final TObjectHashingStrategy<T> hashing_strategy = _hashingStrategy;

        final Object[] set = _set;
        final int length = set.length;
        final int hash = hashing_strategy.computeHashCode(o) & 0x7fffffff;
        int index = hash % length;
        Object cur = set[index];

        if (cur == FREE) {
            return index;       
        } else if (cur != REMOVED && hashing_strategy.equals((T) cur, o)) {
            return -index -1;   
        } else {                
            
            final int probe = 1 + (hash % (length - 2));

            
            
            
            
            
            
            
            
            
            
            
            if (cur != REMOVED) {
                
                
                do {
                    index -= probe;
                    if (index < 0) {
                        index += length;
                    }
                    cur = set[index];
                } while (cur != FREE
                         && cur != REMOVED
                         && ! hashing_strategy.equals((T) cur, o));
            }

            
            
            
            if (cur == REMOVED) {
                int firstRemoved = index;
                while (cur != FREE
                       && (cur == REMOVED || ! hashing_strategy.equals((T) cur, o))) {
                    index -= probe;
                    if (index < 0) {
                        index += length;
                    }
                    cur = set[index];
                }
                
                return (cur != FREE) ? -index -1 : firstRemoved;
            }
            
            
            return (cur != FREE) ? -index -1 : index;
        }
    }