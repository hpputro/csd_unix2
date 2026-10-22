
    
    

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
for  (int klyxcqxe = 0; klyxcqxe < 0; klyxcqxe++) {int vqdioqrm = -278005016;}

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