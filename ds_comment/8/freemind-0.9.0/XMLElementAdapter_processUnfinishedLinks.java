/** Completes the links within the getMap(). They are registered in the register.*/ public void processUnfinishedLinks(MindMapLinkRegistry register) {
        // add labels to the nodes:
        setIDs(mIDToTarget, register);
        // complete arrow links with right labels:
        for(int i = 0; i < mArrowLinkAdapters.size(); ++i) {
            ArrowLinkAdapter arrowLink = (ArrowLinkAdapter) mArrowLinkAdapters.get(i);
            String oldID = arrowLink.getDestinationLabel();
            NodeAdapter target = null;
            String newID = null;
            // find oldID in target list:
            if(mIDToTarget.containsKey(oldID)) {
                // link present in the xml text
                target = (NodeAdapter) mIDToTarget.get(oldID);
                newID = register.getLabel(target);
            } else if(register.getTargetForID(oldID) != null) {
                // link is already present in the getMap() (paste).
                target = (NodeAdapter) register.getTargetForID(oldID);
                if(target == null) {
                    // link target is in nowhere-land
                    logger.severe("Found the label " + oldID + ", but not the corresponding node in the map. The link "+arrowLink+" is not restored.");
                    continue;
                }
                newID = register.getLabel(target);
                if( ! newID.equals(oldID) ) {
                    logger.severe("Servere internal error. Looked for id " + oldID + " but found "+newID + " in the node " + target+".");
                    continue;
                }
            } else {
                // link target is in nowhere-land
                logger.severe("Cannot find the label " + oldID + " in the map. The link "+arrowLink+" is not restored.");
                for (Iterator iterator = mIDToTarget.keySet().iterator(); iterator
						.hasNext();) {
					String id = (String) iterator.next();
					logger.severe("Old-Id: " + id + " = new id: " + ((MindMapNode)mIDToTarget.get(id)).getObjectId(mModeController));
				}
                continue;
            }
            // set the new ID:
            arrowLink.setDestinationLabel(newID);
            // set the target:
            arrowLink.setTarget(target);
            // add the arrowLink:
            //System.out.println("Node = " + target+ ", oldID="+oldID+", newID="+newID);
            register.registerLink(arrowLink);

        }
    }