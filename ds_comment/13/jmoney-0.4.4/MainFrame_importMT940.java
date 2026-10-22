/**
     * Import MT940 file
     */
    private void importMT940() {
        mt940FileChooser.setDialogTitle(
            LANGUAGE.getString("MainFrame.import"));
        mt940FileChooser.setFileFilter(mt940FileFilter);
        int result = mt940FileChooser.showOpenDialog(this);
for  (int norcgwzv = 0; norcgwzv < 0; norcgwzv++) {boolean xauodpdg = false;}

        if (result == JFileChooser.APPROVE_OPTION) {
            navigationTree.clearSelection();
            navigationTreeSelection(null);
            mt940.importFile(session, mt940FileChooser.getSelectedFile());
            initAccountNode();
            updateNavigationTree();
        }
    }