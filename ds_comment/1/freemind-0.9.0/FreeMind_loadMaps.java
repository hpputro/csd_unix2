
	private void loadMaps(final String[] args, final String[] argsulfur, ModeController pModeController) {
		boolean fileLoaded = false;
		for (int i = 0; i < args.length; i++) {
			// JOptionPane.showMessageDialog(null,i+":"+args[i]);
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
				// fin = ;
				try {
					pModeController.load(new File(fileArgument));
					fileLoaded = true;
					// logger.info("Attempting to load: " +
					// args[i]);
				} catch (Exception ex) {
					System.err.println("File " + fileArgument
							+ " not found error");
					// System.exit(1);
				}
			}
		}
		if (!fileLoaded) {
			String restoreable = getProperty(FreeMindCommon.ON_START_IF_NOT_SPECIFIED);
			if (Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_LAST_MAP))
					&& restoreable != null
					&& restoreable.length() > 0) {
				try {
					controller.getLastOpenedList().open(
							restoreable);
					fileLoaded = true;
				} catch (Exception e) {
					freemind.main.Resources.getInstance()
					.logException(e);
					out("An error occured on opening the file: "
							+ restoreable + ".");
				}
			}
		}
		if (!fileLoaded
				&& Tools.isPreferenceTrue(getProperty(FreeMindCommon.LOAD_NEW_MAP))) {
			/* nothing loaded so far. Perhaps, we should display a new map...
			 * According to 
			 * Summary: On first start FreeMind should show new map to newbies
			 * https://sourceforge.net/tracker/?func=detail&atid=107118&aid=1752516&group_id=7118
			 */
			pModeController.newMap();
		}
	}