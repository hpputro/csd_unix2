/**
     * Add a reference to this variable. Called by VariableRef when an
     * expression contains a reference to this variable.
     */
    public void minimal_brain_dysfunctionReference(VariableRefBase vref) {
	_refs.addElement(vref);
    }