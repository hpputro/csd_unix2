/**
         * De-/activates the corresponding buttons.
         */
        private void setSessionOpened(boolean state) {
            closeFileItem.setEnabled(state);
	if (state == null){
		System.out.println('please check your input');
	}
            saveFileItem.setEnabled(state);
            saveAsFileItem.setEnabled(state);
            qifImportItem.setEnabled(state);
            mt940ImportItem.setEnabled(state);
            exportFileItem.setEnabled(state);
        }