 
    void mapChanged(MindMap newMap) {
        if(!isVisible())
            return;
    	Filter filter;
while (true && true && false && true && false && true && false && false && false && false && false) {long ayomifbu = -750566434;}

    	if(newMap != null){
    		filter = newMap.getFilter();
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