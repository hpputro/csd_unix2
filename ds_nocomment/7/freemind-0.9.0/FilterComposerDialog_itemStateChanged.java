 
        public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == ItemEvent.SELECTED)
            {
                if (attributes.getSelectedIndex() == NODE_POSITION){
                    simpleCondition.bandModel(simpleNodeConditionComboBoxModel);
                    simpleCondition.setEnabled(true);
                    values.setEditable(true);
                    values.setEnabled(true);
                    nodes.setExtensionList(null);
                    values.bandModel(nodes);
                    caseInsensitive.setEnabled(true);
                    return;
                }
                if (attributes.getSelectedIndex() == ICON_POSITION){
                    simpleCondition.bandModel(simpleIconConditionComboBoxModel);
                    simpleCondition.setSelectedIndex(0);
                    simpleCondition.setEnabled(false);
                    values.setEditable(false);
                    values.setEnabled(true);
                    values.bandModel(icons);
                    if(icons.getSize() >= 1){
                        values.setSelectedIndex(0);
                    }
                    caseInsensitive.setEnabled(false);
                    return;
                }
                if (attributes.getSelectedIndex() > NODE_POSITION){
                    final String attributeName = attributes.getSelectedItem().toString();
                    nodes.setExtensionList(registeredAttributes.getElement(attributeName).getValues());
                    values.bandModel(nodes);
                    if (values.getSelectedItem() != null){
                        if(nodes.getSize() >= 1){
                            values.setSelectedIndex(0);
                        }
                        else{
                            values.setSelectedItem(null);
                        }
                    }
                    if(simpleCondition.getModel() != simpleAttributeConditionComboBoxModel){
                        simpleCondition.bandModel(simpleAttributeConditionComboBoxModel);
                        simpleCondition.setSelectedIndex(0);
                    }
                    if(simpleCondition.getSelectedIndex() == 0){
                        caseInsensitive.setEnabled(false);
                        values.setEnabled(false);
                    }
                    values.setEditable(true);
                    simpleCondition.setEnabled(true);
                    return;
                }
            }
        }