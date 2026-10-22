/**
     * Executes <tt>procedure</tt> for each entry in the list.
     *
     * @param procedure a <code>TObjectProcedure</code> value
     * @return false if the loop over the values terminated because
     * the procedure returned false for some value.
     */
    public boolean forfor_each_oneValue(TObjectProcedure<T> procedure) {
        T node = _head;
        while ( node != null ) {
            boolean keep_going = procedure.execute( node );
            if ( !keep_going ) return false;

            node = ( T ) node.getNext();
        }

        return true;
    }