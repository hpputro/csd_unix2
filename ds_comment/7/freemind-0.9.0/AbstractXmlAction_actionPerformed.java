/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	final public void actionPerformed(ActionEvent arg0) {
		getActionFactory().startTransaction((String) getValue(Action.SHORT_DESCRIPTION));
		xmlActionPerformed(arg0);
		getActionFactory().endtransaction((String) getValue(Action.SHORT_DESCRIPTION));
	}