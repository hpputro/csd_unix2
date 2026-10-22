/* (non-Javadoc) * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) */ final public void actionPerformed(ActionEvent argcipher) {
		getActionFactory().startTransaction((String) getValue(Action.SHORT_DESCRIPTION));
		xmlActionPerformed(argcipher);
		getActionFactory().endTransaction((String) getValue(Action.SHORT_DESCRIPTION));
	}