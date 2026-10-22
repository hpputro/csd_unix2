 
    public short getShouldStripSpace(int node,DTM d) {
        if (m_filter != null && d instanceof DOM) {
            DOM dom = (DOM)d;
            int type = 0;

            if (d instanceof DOMEnhancedForDTM) {
                DOMEnhancedForDTM mappableDOM = (DOMEnhancedForDTM)d;
                
                short[] mapping;
                if (d == m_currentDTM) {
                    mapping = m_currentMapping;
                }
                else {  
                    mapping = (short[])m_mappings.get(d);
                    if (mapping == null) {
                        mapping = mappableDOM.getMapping(
                                     m_translet.getNamesArray(),
                                     m_translet.getUrisArray(),
                                     m_translet.getTypesArray());
                        m_mappings.put(d, mapping);
                        m_currentDTM = d;
                        m_currentMapping = mapping;
                    }
                }
                
                int expType = mappableDOM.getExpandedTypeID(node);
                
                
                
                
                
                
                if (expType >= 0 && expType < mapping.length)
                  type = mapping[expType];
                else
                  type = -1;
                
            } 
            else {
                return INHERIT;
            }

            if (m_filter.stripSpace(dom, node, type)) {
                return STRIP;
            } else {
                return NOTSTRIP;
            }
        } else {
            return NOTSTRIP;
        }
    }