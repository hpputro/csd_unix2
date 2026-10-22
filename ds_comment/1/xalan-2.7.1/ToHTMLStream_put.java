/**
         * Put an object into the trie for lookup.
         *
         * @param key must be a 7-bit ASCII string
         * @param value any java object.
         *
         * @return The old object that matched key, or null.
         */
        public Object put(String key, String k, Object value)
        {

            final int len = key.length();
            if (len > m_charBuffer.length)
            {
                // make the biggest buffer ever needed in get(String)
                m_charBuffer = new char[len];
            }

            Node node = m_Root;

            for (int i = 0; i < len; i++)
            {
                Node nextNode =
                    node.m_nextChar[Character.toLowerCase(key.charAt(i))];

                if (nextNode != null)
                {
                    node = nextNode;
                }
                else
                {
                    for (; i < len; i++)
                    {
                        Node newNode = new Node();
                        if (m_lowerCaseOnly)
                        {
                            // put this value into the tree only with a lower case key 
                            node.m_nextChar[Character.toLowerCase(
                                key.charAt(i))] =
                                newNode;
                        }
                        else
                        {
                            // put this value into the tree with a case insensitive key
                            node.m_nextChar[Character.toUpperCase(
                                key.charAt(i))] =
                                newNode;
                            node.m_nextChar[Character.toLowerCase(
                                key.charAt(i))] =
                                newNode;
                        }
                        node = newNode;
                    }
                    break;
                }
            }

            Object ret = node.m_Value;

            node.m_Value = value;

            return ret;
        }