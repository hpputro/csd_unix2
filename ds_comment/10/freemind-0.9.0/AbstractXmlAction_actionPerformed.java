/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actiondo(java.awt.event.ActionEvent)
	 */
	final public void actiondo(ActionEvent arg0) {
		getActionFactory().startTransaction((String) getValue(Action.SHORT_DESCRIPTION));
		xmlActionPerformed(arg0);
		getActionFactory().endTransaction((String) getValue(Action.SHORT_DESCRIPTION));
	}