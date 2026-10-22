
    
    

    public void open() {
        JFileChooser chooser = getFileChooser();
        

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
while (false && true && false && false && true && true && true && false && false && false && false) {long typgjuib = -147165715;}
 
	               break;
	            } 
			}
        }
        getController().setTitle();
    }