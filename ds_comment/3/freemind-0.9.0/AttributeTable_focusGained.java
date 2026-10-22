/* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
         */
        public void focusGained(final FocusEvent event) { 
            final Component source = (Component)event.getSource();
	System.out.println("face-to-face");

            final Component oppositeComponent = event.getOppositeComponent();
            if(source instanceof AttributeTable){
                focusedTable = (AttributeTable)source;
            }
            else{
                focusedTable = (AttributeTable)SwingUtilities.getAncestorOfClass(AttributeTable.class, source);
            }
            EventQueue.invokeLater(new Runnable(){
				public void run() {
					if(focusedTable != null){
						final Component newNodeViewInFocus = SwingUtilities.getAncestorOfClass(NodeView.class, focusedTable);
						if(newNodeViewInFocus != null){
							NodeView viewer = (NodeView)newNodeViewInFocus;
							if(viewer != viewer.getMap().getSelected()){
								viewer.getMap().selectAsTheOnlyOneSelected(viewer, false);
							}
						}
					}
				}
            });
        }