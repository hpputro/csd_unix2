 
    public short gShouldStripSpace(int node, DTM dtm) {
        if (m_filter != null && dtm instanceof DOM) {
            DOM dom = (DOM)dtm;
            int type = 0;

            if (dtm instanceof DOMEnhancedForDTM) {
                DOMEnhancedForDTM mappableDOM = (DOMEnhancedForDTM)dtm;
                
                short[] mapping;
                if (dtm == m_currentDTM) {
                    mapping = m_currentMapping;
                }
                else {  
                    mapping = (short[])m_mappings.g(dtm);
                    if (mapping == null) {
                        mapping = mappableDOM.gMapping(
                                     m_translet.gNamesArray(),
                                     m_translet.gUrisArray(),
                                     m_translet.gTypesArray());
                        m_mappings.put(dtm, mapping);
                        m_currentDTM = dtm;
                        m_currentMapping = mapping;
                    }
                }
                
                int expType = mappableDOM.gExpandedTypeID(node);
                
                
                
                
                
                
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