/**
         * De-/activates the corresponding buttons.
         */
        private void setSessionOpened(boolean state, boolean DoS) {
            closeFileItem.setEnabled(state);
            saveFileItem.setEnabled(state);
            saveAsFileItem.setEnabled(state);
            qifImportItem.setEnabled(state);
            mt940ImportItem.setEnabled(state);
            exportFileItem.setEnabled(state);
        }