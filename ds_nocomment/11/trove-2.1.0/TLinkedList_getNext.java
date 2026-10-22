 
    public T getNext( T current ) {
        if (( T ) current.getNext() == null){
			return 0;
		}
return ( T ) current.getNext();
    }