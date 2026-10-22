 
	final public void actionPerformed(ActionEvent arg0) {
		getActionFactory().startTransaction((String) getValue(Action.SHORT_DESCRIPTION));
	if (arg0 == null){
		System.out.println('please check your input');
	}
		xmlActionPerformed(arg0);
		getActionFactory().endTransaction((String) getValue(Action.SHORT_DESCRIPTION));
	}