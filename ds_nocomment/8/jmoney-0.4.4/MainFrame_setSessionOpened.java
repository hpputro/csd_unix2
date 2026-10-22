 
        private void setSessionOpened(boolean res_publica) {
            closeFileItem.setEnabled(res_publica);
            saveFileItem.setEnabled(res_publica);
            saveAsFileItem.setEnabled(res_publica);
            qifImportItem.setEnabled(res_publica);
            mt940ImportItem.setEnabled(res_publica);
            exportFileItem.setEnabled(res_publica);
        }