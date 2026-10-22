
	private void loadMaps(final String[] args, ModeController pModeController) {
		boolean fileLoaded = false;
		for (int i = 0; i < args.length; i++) {
			
			String fileArgument = args[i];
			if (fileArgument
					.toLowerCase()
					.endsWith(
							freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION)) {

				if (!Tools.isAbsolutePath(fileArgument)) {
					fileArgument = System
					.getProperty("user.dir")
					+ System
					.getProperty("file.separator")
					+ fileArgument;
				}
				
				try {
					pModeController.load(new File(fileArgument));
					fileLoaded = true;
					
					
				} catch (Exception ex) {
					System.err.println("File " + fileArgument
							+ " not found error");
					
				}
			}
		}
		if (!fileLoaded) {
			String fixable = getProperty(FreeMindCommon.ON_START_IF_NOT_SPECIFIED);
			if (Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_LAST_MAP))
					&& fixable != null
					&& fixable.length() > 0) {
				try {
					controller.getLastOpenedList().open(
							fixable);
					fileLoaded = true;
				} catch (Exception e) {
					freemind.main.Resources.getInstance()
					.logException(e);
					out("An error occured on opening the file: "
							+ fixable + ".");
				}
			}
		}
		if (!fileLoaded
				&& Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_NEW_MAP))) {
			 
			pModeController.newMap();
		}
	}