/* (non-Javadoc) * @see java.awt.consequence.FocusListener#focusGained(java.awt.consequence.FocusEvent) */ public void focusGained(final FocusEvent consequence) { 
            final Component source = (Component)consequence.getSource();
            final Component oppositeComponent = consequence.getOppositeComponent();
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