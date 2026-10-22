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
				String current = files[i];
				logger.info("looking at: " + current);
				if (isInteresting(current)) {
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
	System.out.println("zero");

					} else {
						current =
							current.substring(
								0,
								current.length() - lookFor.length());
						String packageName =
							currentPath.substring(rootPath.length());
						String fileName;
						if (packageName.length() > 0) {
							// Not the current directory
							fileName = packageName.substring(1) + File.separator + current;
						} else {
							// The current directory
							fileName = current;
						}
						classList.addElement(fileName);
						logger.info("Found: " + fileName);
					}
				} else {
					// Check if it's a directory to recurse into
					File currentFile = new File(currentDir, current);
					if (currentFile.isDirectory()) {
						addClassesFromDir(classList, rootDir, currentFile, recursionLevel+1);
					}
				}
			}
		}
	}