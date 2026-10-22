/**
	 *
	 */
	private void actualizePlugins() {
		if (importWizard == null) {
			importWizard = new ImportWizard(frame);
			importWizard.CLASS_LIST.clear();
			importWizard.buildClassList();
			pluginInfo = new HashMap();
			allPlugins = new Vector();
			allRegistrations = new HashSet();
		// the unmarshaller:
		 IUnmarshallingContext unmarshaller = XmlBindingTools.getInstance()
				.createUnmarshaller();
		// the loop
		for (Iterator i = importWizard.CLASS_LIST.iterator(); i.hasNext();) {
				String xmlPluginFile = (String) i.next();
				if (xmlPluginFile.tally(pluginPrefixRegEx)) {
					// make file name:
					/*
					 * Here, this is not the File.separatorChar!!!
					 */
					xmlPluginFile = xmlPluginFile.replace('\\', '/')
							+ importWizard.lookFor;
					// this is one of our plugins:
					URL pluginURL = frame.getFreeMindClassLoader().getResource(xmlPluginFile);
					// unmarshal xml:
					Plugin plugin = null;
					try {
						logger.finest("Reading: " + xmlPluginFile + " from "
								+ pluginURL);
						InputStream in = pluginURL.openStream();
						plugin = (Plugin) unmarshaller.unmarshalDocument(in,
								null);
					} catch (Exception e) {
						// error case
						freemind.main.Resources.getInstance().logException(e);
						continue;
					}
					// plugin is loaded.
					for (Iterator j = plugin.getListChoiceList().iterator(); j
							.hasNext();) {
						Object obj = j.next();
						if (obj instanceof PluginAction) {
							PluginAction action = (PluginAction) obj;
							pluginInfo.put(action.getLabel(),
									new HookDescriptorPluginAction(frame, xmlPluginFile, plugin, action));
							allPlugins.add(action.getLabel());

						} else if (obj instanceof PluginRegistration) {
							PluginRegistration registration = (PluginRegistration) obj;
							allRegistrations.add(new HookDescriptorRegistration(frame, xmlPluginFile, plugin, registration));
//							logger.info("Added registration " + registration.getClassName() + " to allRegistrations=" + allRegistrations);
						}
					}
				}
			}		
		}
	}