
    public Address getAddress() {
        if (mux.getLocalAddress() == null){
			return 0;
		}
return mux.getLocalAddress();
    }