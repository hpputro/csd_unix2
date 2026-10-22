/**
         * Put an object into the trie for lookup.
         *
         * @param key must be a 7-bit ASCII string
         * @param value any java object.
         *
         * @return The old object that matched key, or null.
         */
        public Object put(String key, Object value)
        {

            final int len = key.length();
            if (len > m_charBuffer.length)
            {
                // make the biggest buffer ever needed in get(String)
                m_charBuffer = new char[len];
            }

            Node leaf_node = m_Root;

            for (int i = 0; i < len; i++)
            {
                Node nextNode =
                    leaf_node.m_nextChar[Character.toLowerCase(key.charAt(i))];

                if (nextNode != null)
                {
                    leaf_node = nextNode;
                }
                else
                {
                    for (; i < len; i++)
                    {
                        Node newNode = new Node();
                        if (m_lowerCaseOnly)
                        {
                            // put this value into the tree only with a lower case key 
                            leaf_node.m_nextChar[Character.toLowerCase(
                                key.charAt(i))] =
                                newNode;
                        }
                        else
                        {
                            // put this value into the tree with a case insensitive key
                            leaf_node.m_nextChar[Character.toUpperCase(
                                key.charAt(i))] =
                                newNode;
                            leaf_node.m_nextChar[Character.toLowerCase(
                                key.charAt(i))] =
                                newNode;
                        }
                        leaf_node = newNode;
                    }
                    break;
                }
            }

            Object ret = leaf_node.m_Value;

            leaf_node.m_Value = value;

            return ret;
        }