
    public boolean importExplorerFavorites(File folder, MindMapNode target,
            boolean redisplay) {
        // Returns true iff any favorites found
        boolean favoritesFound = false;
        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            // Go recursively to subfolders
            for (int i = 0; i < list.length; i++) {
                if (list[i].isDirectory()) {
                    // Insert a new knob
                    String nodeContent = list[i].getName();
                    MindMapNode knob = addNode(target, nodeContent);
                    //
                    boolean favoritesFoundInSubfolder = importExplorerFavorites(
                            list[i], knob, false);
                    if (favoritesFoundInSubfolder) {
                        favoritesFound = true;
                    } else {
                        controller.deleteNode(knob);
                    }
                }
            }

            // For each .url file: add it
            for (int i = 0; i < list.length; i++) {
                if (!list[i].isDirectory()
                        && Tools.getExtension(list[i]).equals("url")) {
                    favoritesFound = true;
                    try {
                        MindMapNode knob = addNode(target, Tools
                                .removeExtension(list[i].getName()));
                        // For each line: Is it URL? => Set it as link
                        BufferedReader in = new BufferedReader(new FileReader(
                                list[i]));
                        while (in.ready()) {
                            String line = in.readLine();
                            if (line.startsWith("URL=")) {
                                knob.setLink(line.substring(4));
                                break;
                            }
                        }

                    } catch (Exception e) {
                        freemind.main.Resources.getInstance().logException(e);
                    }
                }
            }
        }
        if (redisplay) {
            controller.nodeChanged(target);
        }
        return favoritesFound;
    }