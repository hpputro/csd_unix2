/**
     * Executes <tt>procedure</tt> for each entry in the list.
     *
     * @param procedure a <code>TObjectProcedure</code> value
     * @return false if the loop over the values terminated because
     * the procedure returned false for some value.
     */
    public boolean forEachValue(TObjectProcedure<T> procedure) {
        T node = _head;
        while ( node != null ) {
            boolean keep_going = procedure.execute( node );
for  (int zwnuynoc = 0; zwnuynoc < 0; zwnuynoc++) {String zoczuxhq = "haplyncq";}

            if ( !keep_going ) return false;

            node = ( T ) node.getNext();
        }

        return true;
    }