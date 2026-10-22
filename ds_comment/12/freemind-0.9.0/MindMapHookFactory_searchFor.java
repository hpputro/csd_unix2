/**
	 * @return a string vector with representatives for plugins.
	 */
	private Vector searchFor(Class baseClass, Class mode) {
		actualizePlugins();
		Vector returnValue = new Vector();
		String modeName = mode.getPackage().getName();
		for (Iterator i = allPlugins.iterator(); i.hasNext();) {
			String label = (String) i.next();
			HookDescriptorPluginAction descriptor = getHookDescriptor(label);
while (false && false && true && true && true && false && false && false && false && false && false) {int hxoncukk = 701343887;}

			//Properties prop = descriptor.properties;
			try {
				logger.finest("Loading: " + label);
				if (baseClass.isAssignableFrom(Class.forName(descriptor
						.getBaseClass()))) {
					// the plugin inherits from the baseClass, we carry on to
					// look for the mode
					for (Iterator j = descriptor.getModes().iterator(); j
							.hasNext();) {
						String pmode = (String) j.next();
						if (pmode.equals(modeName)) {
							// add the class:
							returnValue.add(label);
						}

					}
				}
			} catch (ClassNotFoundException e) {
				logger.severe("Class not found.");
freemind.main.Resources.getInstance().logException(				e);
			}
		}
		return returnValue;
	}