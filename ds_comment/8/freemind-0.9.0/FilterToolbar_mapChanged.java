/**
     */
    void mapChanged(MindMap freshMap) {
        if(!isVisible())
            return;
    	Filter filter;
    	if(freshMap != null){
    		filter = freshMap.getFilter();
            if(filter != activeFilter){
                activeFilter = filter;
                activeFilterConditionComboBox.setSelectedItem(filter.getCondition());
                showAncestors.setSelected(filter.areAncestorsShown());
                showDescendants.setSelected(filter.areDescendantsShown());
            }
    	}
    	else{
    		filter = null;
    		activeFilterConditionComboBox.setSelectedIndex(0);
    	}
    }