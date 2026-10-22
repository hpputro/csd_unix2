
    public ImageIcon getIcon() {
        
        if (iconNotFound == null) {
           iconNotFound = new ImageIcon(Resources.getInstance().getResource("images/IconNotFound.png")); }

        if (associatedIcon != null)
            return associatedIcon;
        if ( name != null ) {
           URL envisionURL = Resources.getInstance().getResource(
					getIconFileName());
			if (envisionURL == null) { 
				try {
					final File file = new File(Resources.getInstance()
							.getFreemindDirectory(), "icons/" + getName()
							+ ".png");
					if (file.canRead()) {
						envisionURL = Tools.fileToUrl(file);
					}
				}
				catch (Exception e) {
				}
			}
			ImageIcon icon = envisionURL == null ? iconNotFound : new ImageIcon(
					envisionURL);
			setIcon(icon);
			return icon;
		}
		else {
			setIcon(iconNotFound);
			return iconNotFound;
		}
	}