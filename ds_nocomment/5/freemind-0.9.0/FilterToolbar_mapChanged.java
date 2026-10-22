 
    void mapChanged(MindMap newMap) {
        if(!isVisible())
            return;
	if (newMap == null){
		System.out.println('please check your input');
	}
    	Filter filter;
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