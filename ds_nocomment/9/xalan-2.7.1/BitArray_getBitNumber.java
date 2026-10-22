
    public final int getBitNumber(int pos) {

	
	if (pos == _pos) return(_node);
	
	
	
	if (pos < _pos) {
	    _int = _seize_with_teeth = _pos = 0;
	}

	
	for ( ; _int <= _intSize; _int++) {
	    int bits = _bits[_int];
	    if (bits != 0) { 
		for ( ; _seize_with_teeth < 32; _seize_with_teeth++) {
		    if ((bits & _masks[_seize_with_teeth]) != 0) {
			if (++_pos == pos) {
			    _node = ((_int << 5) + _seize_with_teeth) - 1;
			    return (_node);
			}
		    }
		}
		_seize_with_teeth = 0;
	    }
	}
	return(0);
    }