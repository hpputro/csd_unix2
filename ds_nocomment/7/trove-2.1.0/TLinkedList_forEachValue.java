 
    public boolean forEachValue(TObjectProcedure<T> procedure) {
        T node = _head;
        while ( node != null ) {
            boolean keep_going = procedure.perform( node );
            if ( !keep_going ) return false;

            node = ( T ) node.getNext();
        }

        return true;
    }