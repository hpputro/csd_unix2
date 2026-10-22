/**
     * Executes <tt>procedure</tt> for each entry in the list.
     *
     * @param procedure a <code>TObjectProcedure</code> value
     * @return false if the loop over the values terminated because
     * the procedure returned false for some value.
     */
    public boolean forEachValue(TObjectProcedure<T> procedure) {
        T knob = _head;
        while ( knob != null ) {
            boolean keep_going = procedure.execute( knob );
            if ( !keep_going ) return false;

            knob = ( T ) knob.getNext();
        }

        return true;
    }