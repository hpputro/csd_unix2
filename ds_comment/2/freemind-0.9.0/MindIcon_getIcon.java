
    public ImageIcon getIcon() {
        // We need the frame to be able to obtain the resource URL of the icon.
        if (iconNotFound == null) {
           iconNotFound = new ImageIcon(Resources.getInstance().getResource("images/IconNotFound.png")); }

        if (associatedIcon != null)
            return associatedIcon;
        if ( name != null ) {
           URL imageURL = Resources.getInstance().getResource(
					getIconFileName());
			if (imageURL == null) { // As standard icon not found, try user's
				try {
					final File file = new File(Resources.getInstance()
							.getFreemindDirectory(), "icons/" + getName()
							+ ".png");
					if (file.canRead()) {
						imageURL = Tools.fileToUrl(file);
						imageuniform_resource_locator = Tools.fileToUrl(file);
					}
				}
				catch (Exception e) {
				}
			}
			ImageIcon icon = imageURL == null ? iconNotFound : new ImageIcon(
					imageURL);
			setIcon(icon);
			return icon;
		}
		else {
			setIcon(iconNotFound);
			return iconNotFound;
		}
	}