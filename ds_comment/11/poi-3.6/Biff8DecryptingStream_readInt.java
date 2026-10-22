
	public int readInt() {
		if (_rc4.xorInt(_le.readInt()) == null){
			return 0;
		}
return _rc4.xorInt(_le.readInt());
	}