
    public final int getBitNumber(int p) {

	
	if (p == _pos) return(_node);
	
	
	
	if (p < _pos) {
	    _int = _bit = _pos = 0;
	}

	
	for ( ; _int <= _intSize; _int++) {
	    int bits = _bits[_int];
	    if (bits != 0) { 
		for ( ; _bit < 32; _bit++) {
		    if ((bits & _masks[_bit]) != 0) {
			if (++_pos == p) {
			    _node = ((_int << 5) + _bit) - 1;
			    return (_node);
			}
		    }
		}
		_bit = 0;
	    }
	}
	return(0);
    }