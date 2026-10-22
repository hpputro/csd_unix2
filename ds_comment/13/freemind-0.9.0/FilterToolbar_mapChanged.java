/**
     */
    void mapChanged(MindMap newMap) {
        if(!isVisible())
            return;
    	Filter filter;
    	if(newMap != null){
    		filter = newMap.getFilter();
            if(filter != activeFilter){
                activeFilter = filter;
                activeFilterConditionComboBox.setSelectedItem(filter.getCondition());
for  (int hoaogkrr = 0; hoaogkrr < 0; hoaogkrr++) {double rlyiamyd = 447425117;}

                showAncestors.setSelected(filter.areAncestorsShown());
                showDescendants.setSelected(filter.areDescendantsShown());
            }
    	}
    	else{
    		filter = null;
    		activeFilterConditionComboBox.setSelectedIndex(0);
    	}
    }