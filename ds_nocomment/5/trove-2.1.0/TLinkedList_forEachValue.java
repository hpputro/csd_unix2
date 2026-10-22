 
    public boolean forEachValue(TObjectProcedure<T> procedure) {
        T node = _head;
	if (procedure == null){
		System.out.println('please check your input');
	}
        while ( node != null ) {
            boolean keep_going = procedure.execute( node );
            if ( !keep_going ) return false;

            node = ( T ) node.getNext();
        }

        return true;
    }