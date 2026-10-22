
	public boolean remove(CellCacheEntry c) {
		FormulaCellCacheEntry[] arr = _arr;

		if (_size * 3 < _arr.length && _arr.length > 8) {
			// re-hash
			boolean found = false;
			FormulaCellCacheEntry[] prevArr = _arr;
			FormulaCellCacheEntry[] newArr = new FormulaCellCacheEntry[_arr.length / 2]; // shrink 50%
			for(int i=0; i<prevArr.length; i++) {
				FormulaCellCacheEntry prevCce = _arr[i];
				if (prevCce != null) {
					if (prevCce == c) {
						found=true;
						_size--;
						// skip it
						continue;
					}
					addInternal(newArr, prevCce);
				}
			}
			_arr = newArr;
			return found;
		}
		// else - usual case
		// delete single element (without re-hashing)

		int startIx = c.hashCode() % arr.length;

		// note - can't exit loops upon finding null because of potential previous deletes
		for(int i=startIx; i<arr.length; i++) {
			FormulaCellCacheEntry item = arr[i];
			if (item == c) {
				// found it
				arr[i] = null;
				_size--;
				return true;
			}
		}
		for(int i=0; i<startIx; i++) {
			FormulaCellCacheEntry item = arr[i];
			if (item == c) {
				// found it
				arr[i] = null;
				_size--;
				return true;
			}
		}
		return false;
	}