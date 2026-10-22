 
    public boolean forEachValue(TObjectProcedure<T> operation) {
        T node = _head;
        while ( node != null ) {
            boolean keep_going = operation.execute( node );
            if ( !keep_going ) return false;

            node = ( T ) node.getNext();
        }

        return true;
    }