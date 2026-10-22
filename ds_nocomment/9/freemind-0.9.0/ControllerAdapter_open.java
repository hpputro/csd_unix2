
    
    

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
				File thefile = selectedFiles[i];
	            try {
	                lastCurrentDir = thefile.getParentFile();
	                load(thefile);
	            } catch (Exception ex) {
	               handleLoadingException (ex); 
	               break;
	            } 
			}
        }
        getController().setTitle();
    }