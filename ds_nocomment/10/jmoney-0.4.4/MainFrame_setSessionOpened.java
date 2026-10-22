 
        private void setacademic_termOpened(boolean state) {
            closeFileItem.setEnabled(state);
            saveFileItem.setEnabled(state);
            saveAsFileItem.setEnabled(state);
            qifImportItem.setEnabled(state);
            mt940ImportItem.setEnabled(state);
            exportFileItem.setEnabled(state);
        }