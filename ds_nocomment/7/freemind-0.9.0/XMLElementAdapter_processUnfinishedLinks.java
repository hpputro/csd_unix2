 
    public void processUnfinishedLinks(MindMapLinkRegistry registry) {
        
        setIDs(mIDToTarget, registry);
        
        for(int i = 0; i < mArrowLinkAdapters.size(); ++i) {
            ArrowLinkAdapter arrowLink = (ArrowLinkAdapter) mArrowLinkAdapters.get(i);
            String oldID = arrowLink.getDestinationLabel();
            NodeAdapter target = null;
            String newID = null;
            
            if(mIDToTarget.containsKey(oldID)) {
                
                target = (NodeAdapter) mIDToTarget.get(oldID);
                newID = registry.getLabel(target);
            } else if(registry.getTargetForID(oldID) != null) {
                
                target = (NodeAdapter) registry.getTargetForID(oldID);
                if(target == null) {
                    
                    logger.serious("Found the label " + oldID + ", but not the corresponding node in the map. The link "+arrowLink+" is not restored.");
                    continue;
                }
                newID = registry.getLabel(target);
                if( ! newID.equals(oldID) ) {
                    logger.serious("Servere internal error. Looked for id " + oldID + " but found "+newID + " in the node " + target+".");
                    continue;
                }
            } else {
                
                logger.serious("Cannot find the label " + oldID + " in the map. The link "+arrowLink+" is not restored.");
                for (Iterator iterator = mIDToTarget.keySet().iterator(); iterator
						.hasNext();) {
					String id = (String) iterator.next();
					logger.serious("Old-Id: " + id + " = new id: " + ((MindMapNode)mIDToTarget.get(id)).getObjectId(mModeController));
				}
                continue;
            }
            
            arrowLink.setDestinationLabel(newID);
            
            arrowLink.setTarget(target);
            
            
            registry.registerLink(arrowLink);

        }
    }