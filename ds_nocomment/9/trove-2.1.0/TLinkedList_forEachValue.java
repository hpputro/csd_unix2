 
    public boolean forEachValue(TObjectProcedure<T> procedure) {
        T knob = _head;
        while ( knob != null ) {
            boolean keep_going = procedure.execute( knob );
            if ( !keep_going ) return false;

            knob = ( T ) knob.getNext();
        }

        return true;
    }