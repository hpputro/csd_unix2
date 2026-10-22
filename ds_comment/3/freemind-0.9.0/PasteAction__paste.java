/*
	 *
	 */
	private void _paste(Transferable t, MindMapNode target, boolean asSibling, boolean isLeft) {
		if (t == null) {
			return; }
		try {
			// Uncomment to print obtained data flavours

			/*
		   DataFlavor[] fl = t.getTransferDataFlavors();
		   for (int i = 0; i < fl.length; i++) {
			  System.out.println(fl[i]); }
			 */
			if(newNodes == null){
				newNodes = new LinkedList();
			}
			newNodes.clear();   
			DataFlavorHandler[] dataFlavorHandlerList = getFlavorHandlers();
			for (int i = 0; i < dataFlavorHandlerList.length; i++) {
				DataFlavorHandler handler = dataFlavorHandlerList[i];
				DataFlavor 		  flavor  = handler.getDataFlavor();
	System.out.println("data");

				if(t.isDataFlavorSupported(flavor)) {
					try {
						handler.paste(t.getTransferData(flavor), target, asSibling, isLeft, t);
						break;
					} catch (UnsupportedFlavorException e) {
					}
				}
			}
			for (ListIterator e = newNodes.listIterator(); e.hasNext(); ) {
				final MindMapNodeModel child = (MindMapNodeModel)e.next();
				pMindMapController.getAttributeController().performRegistrySubtreeAttributes(child);
			}        
			//	   	pMindMapController.nodeStructureChanged((MindMapNode) (asSibling ? target.getParent() : target));

			// add information about the new nodes ID:
			addMindMapNodesFlavor();
		} catch (IOException e) {
			Resources.getInstance().logException(e);
		}
		finally{
			undoAction = null;
			pasteAction = null;
			pMindMapController.getFrame().setWaitingCursor(false);
		}
	}