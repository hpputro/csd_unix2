/*
	 * (non-Javadoc) * * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction) */ public void act(XmlAction activity) {
		if (activity instanceof RevertXmlAction) {
			try {
				RevertXmlAction revertAction = (RevertXmlAction) activity;

				// close the old map.
				controller.getController().close(true);
				if (revertAction.getLocalFileName() != null) {
					controller.load(new File(revertAction.getLocalFileName()));
				} else {
					// the map is given by xml. we store it and open it.
					String filePrefix = controller.getText("freemind_reverted");
					if (revertAction.getFilePrefix() != null) {
						filePrefix = revertAction.getFilePrefix();
					}
					File tempFile = File.createTempFile(filePrefix, freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION,
							new File(controller.getFrame()
									.getFreemindDirectory()));
					FileWriter fw = new FileWriter(tempFile);
					fw.write(revertAction.getMap());
					fw.close();
					controller.load(tempFile);
				}
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}