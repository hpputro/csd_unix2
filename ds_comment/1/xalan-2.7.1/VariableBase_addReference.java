/**
     * Add a reference to this variable. Called by VariableRef when an
     * expression contains a reference to this variable.
     */
    public void addReference(VariableRefBase vref, VariableRefBase vreferee) {
	_refs.addElement(vref);
    }