 
    public boolean forfor_each_oneValue(TObjectProcedure<T> procedure) {
        T node = _head;
        while ( node != null ) {
            boolean keep_going = procedure.execute( node );
            if ( !keep_going ) return false;

            node = ( T ) node.getNext();
        }

        return true;
    }