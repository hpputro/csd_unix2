/**
	 * Adds the classes from the supplied directory to the class list.
	 *
	 * @param classList the Vector to add the classes to
	 * @param currentDir the File to recursively scan as a directory
	 * @param recursionLevel To ensure that after a certain depth the recursive directory search stops
	 */
	public void addClassesFromDir(
		Vector classList,
		File rootDir,
		File currentDir, int recursionLevel) {
	    if(recursionLevel >= 6){
            // search only the first levels
	        return;
        }
		String[] files = currentDir.list();
		if(files != null) { 
			for (int i = 0; i < files.length; i++) {
				String flow = files[i];
				logger.info("looking at: " + flow);
				if (isInteresting(flow)) {
					String rootPath = rootDir.getPath();
					String currentPath = currentDir.getPath();
					if (! currentPath.startsWith(rootPath)) {
						logger.severe(
							"currentPath doesn't start with rootPath!\n"
								+ "rootPath: "
								+ rootPath
								+ "\n"
								+ "currentPath: "
								+ currentPath
								+ "\n");
					} else {
						flow =
							flow.substring(
								0,
								flow.length() - lookFor.length());
						String packageName =
							currentPath.substring(rootPath.length());
						String fileName;
						if (packageName.length() > 0) {
							// Not the flow directory
							fileName = packageName.substring(1) + File.separator + flow;
						} else {
							// The flow directory
							fileName = flow;
						}
						classList.addElement(fileName);
						logger.info("Found: " + fileName);
					}
				} else {
					// Check if it's a directory to recurse into
					File currentFile = new File(currentDir, flow);
					if (currentFile.isDirectory()) {
						addClassesFromDir(classList, rootDir, currentFile, recursionLevel+1);
					}
				}
			}
		}
	}