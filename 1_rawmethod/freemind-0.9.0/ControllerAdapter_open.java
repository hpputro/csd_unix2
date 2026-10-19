//
    // Dialogs with user
    //

    public void open() {
        JFileChooser chooser = getFileChooser();
        // fc, 24.4.2008: multi selection has problems as setTitle in Controller doesn't works
//        chooser.setMultiSelectionEnabled(true);
        int returnVal = chooser.showOpenDialog(getView());
        if (returnVal==JFileChooser.APPROVE_OPTION) {
        	File[] selectedFiles;
			if (chooser.isMultiSelectionEnabled()) {
				selectedFiles = chooser.getSelectedFiles();
			} else {
				selectedFiles = new File[]{chooser.getSelectedFile()};
			}
			for (int i = 0; i < selectedFiles.length; i++) {
				File theFile = selectedFiles[i];
	            try {
	                lastCurrentDir = theFile.getParentFile();
	                load(theFile);
	            } catch (Exception ex) {
	               handleLoadingException (ex); 
	               break;
	            } 
			}
        }
        getController().setTitle();
    }