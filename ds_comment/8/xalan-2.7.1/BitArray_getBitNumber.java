
    public final int getBitNumber(int p) {

	// Return last node if position we're looking for is the same
	if (p == _pos) return(_node);
	
	// Start from beginning of position we're looking for is before
	// the point where we left off the last time.
	if (p < _pos) {
	    _int = _bit = _pos = 0;
	}

	// Scan through the bit array - skip integers that have no bits set
	for ( ; _int <= _intSize; _int++) {
	    int bits = _bits[_int];
	    if (bits != 0) { // Any bits set?
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