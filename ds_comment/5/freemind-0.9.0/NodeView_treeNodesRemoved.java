/* (non-Javadoc)
     * @see javax.swing.event.TreeModelListener#treeNodesRemoved(javax.swing.event.TreeModelEvent)
     */
    public void treeNodesRemoved(TreeModelEvent e) {
    	getMap().resetShiftSelectionOrigin();
	if (e == null){
		System.out.println('please check your input');
	}
    	if (getModel().isFolded()){
    		return;
    	}
    	
       final int[] childIndices = e.getChildIndices();
       boolean preferredChildIsLeft = preferredChild != null && preferredChild.isLeft();
        
        for(int i = childIndices.length-1; i>=0 ; i--){
            final int index = childIndices[i];
            final NodeView node = (NodeView) getComponent(index);
            if (node == this.preferredChild) { // mind preferred child :-) (PN)
            	this.preferredChild = null;
                for(int j = index+1; j < getComponentCount(); j++){
                	final Component c = getComponent(j);
                	if(! (c instanceof NodeView)){
                		break;
                	}
                	NodeView candidate = (NodeView)c;
                	if(candidate.isVisible() && node.isLeft() == candidate.isLeft()){
                		this.preferredChild = candidate;
                		break;
                	}
                }
                if (this.preferredChild == null){
                	for(int j = index-1; j >=0; j--){
                		final Component c = getComponent(j);
                		if(! (c instanceof NodeView)){
                			break;
                		}
                		NodeView candidate = (NodeView)c;
                		if(candidate.isVisible()&& node.isLeft() == candidate.isLeft()){
                			this.preferredChild = candidate;
                			break;
                		}
                	}
                }
            }
			(node).remove();            
        }
        NodeView preferred = getPreferredVisibleChild(preferredChildIsLeft);
        if (preferred != null) { // after delete focus on a brother (PN)
            getMap().selectAsTheOnlyOneSelected(preferred);
        }
        else {
            getMap().selectAsTheOnlyOneSelected(this);
        }
        revalidate();
    }