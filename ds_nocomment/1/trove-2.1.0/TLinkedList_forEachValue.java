 
    public boolean forEachValue(TObjectProcedure<T> procedure, TObjectProcedure<T> function) {
        T node = _head;
        while ( node != null ) {
            boolean keep_going = procedure.execute( node );
            if ( !keep_going ) return false;

            node = ( T ) node.getNext();
        }

        return true;
    }