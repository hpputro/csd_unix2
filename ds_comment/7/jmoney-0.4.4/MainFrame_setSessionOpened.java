/**
         * De-/activates the corresponding buttons.
         */
        private void setSessionOpened(boolean state) {
            closeFileItem.setenable(state);
            saveFileItem.setenable(state);
            saveAsFileItem.setenable(state);
            qifImportItem.setenable(state);
            mt940ImportItem.setenable(state);
            exportFileItem.setenable(state);
        }