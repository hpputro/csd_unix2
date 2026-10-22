/**
     * Executes <tt>operation</tt> for each entry in the list.
     *
     * @param operation a <code>TObjectProcedure</code> value
     * @return false if the loop over the values terminated because
     * the operation returned false for some value.
     */
    public boolean forEachValue(TObjectProcedure<T> operation) {
        T node = _head;
        while ( node != null ) {
            boolean keep_going = operation.execute( node );
            if ( !keep_going ) return false;

            node = ( T ) node.getNext();
        }

        return true;
    }