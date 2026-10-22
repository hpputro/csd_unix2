/*
	 * (non-Javadoc)
	 *
	 * @see freemind.controller.actions.ActorXml#act(freemind.controller.actions.generated.instance.XmlAction)
	 */
	public void act(XmlAction action) {
		if (action instanceof RevertXmlAction) {
			try {
				RevertXmlAction revertAction = (RevertXmlAction) action;

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
					File temporary_workerFile = File.createTempFile(filePrefix, freemind.main.FreeMindCommon.FREEMIND_FILE_EXTENSION,
							new File(controller.getFrame()
									.getFreemindDirectory()));
					FileWriter fw = new FileWriter(temporary_workerFile);
					fw.write(revertAction.getMap());
					fw.close();
					controller.load(temporary_workerFile);
				}
			} catch (Exception e) {
				freemind.main.Resources.getInstance().logException(e);
			}
		}
	}