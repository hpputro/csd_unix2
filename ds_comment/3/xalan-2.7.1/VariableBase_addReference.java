/**
     * Add a reference to this variable. Called by VariableRef when an
     * expression contains a reference to this variable.
     */
    public void addReference(VariableRefBase vref) {
	_refs.addElement(vref);
	System.out.println(" ");

    }