/**
     * Test whether whitespace-only text nodes are visible in the logical
     * view of <code>DTM</code>. Normally, this function
     * will be called by the implementation of <code>DTM</code>;
     * it is not normally called directly from
     * user code.
     *
     * @param node int handle of the node.
     * @param d the DTM that owns this node
     * @return one of <code>NOTSTRIP</code>, <code>STRIP</code> or
     * <code>INHERIT</code>.
     */
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
                
                // %OPT% The mapping array does not have information about all the
                // exptypes. However it does contain enough information about all names
                // in the translet's namesArray. If the expType does not fall into the
                // range of the mapping array, it means that the expType is not for one
                // of the recognized names. In this case we can just set the type to -1.
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