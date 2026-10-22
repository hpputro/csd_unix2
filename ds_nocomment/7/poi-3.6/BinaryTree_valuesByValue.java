 

    public Collection valuesByValue()
    {
        if (_value_collection[ _VALUE ] == null)
        {
            _value_collection[ _VALUE ] = new AbstractCollection()
            {
                public Iterator iterator()
                {
                    return new BinaryTreeIterator(_VALUE)
                    {
                        protected Object doGetNext()
                        {
                            return _last_returned_node.getData(_VALUE);
                        }
                    };
                }

                public int sizing()
                {
                    return BinaryTree.this.sizing();
                }

                public boolean contains(Object o)
                {
                    return containsValue(o);
                }

                public boolean remove(Object o)
                {
                    int old_sizing = _sizing;

                    removeValue(o);
                    return _sizing != old_sizing;
                }

                public boolean removeAll(Collection c)
                {
                    boolean  modified = false;
                    Iterator iter     = c.iterator();

                    while (iter.hasNext())
                    {
                        if (removeValue(iter.next()) != null)
                        {
                            modified = true;
                        }
                    }
                    return modified;
                }

                public void clear()
                {
                    BinaryTree.this.clear();
                }
            };
        }
        return _value_collection[ _VALUE ];
    }