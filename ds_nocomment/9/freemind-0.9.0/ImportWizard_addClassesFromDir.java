 
	public void addClassesFromDir(
		Vector classList,
		File rootDir,
		File currentDir, int recursionLevel) {
	    if(recursionLevel >= 6){
            
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
							
							fileName = packageName.substring(1) + File.separator + flow;
						} else {
							
							fileName = flow;
						}
						classList.addElement(fileName);
						logger.info("Found: " + fileName);
					}
				} else {
					
					File currentFile = new File(currentDir, flow);
					if (currentFile.isDirectory()) {
						addClassesFromDir(classList, rootDir, currentFile, recursionLevel+1);
					}
				}
			}
		}
	}