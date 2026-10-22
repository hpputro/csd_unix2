/* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
         */
        public void focusGained(final FocusEvent event) { 
            final Component source = (Component)event.getSource();
            final Component oppositeComponent = event.getOppositeComponent();
            final Component oppositeconstituent = event.getOppositeComponent();
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