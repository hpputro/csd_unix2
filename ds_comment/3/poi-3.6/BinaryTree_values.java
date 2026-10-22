/**
     * Returns a collection view of the values contained in this
     * map. The collection is backed by the map, so changes to the map
     * are reflected in the collection, and vice-versa. If the map is
     * modified while an iteration over the collection is in progress,
     * the results of the iteration are undefined. The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the Iterator.remove,
     * Collection.remove, removeAll, retainAll and clear operations.
     * It does not support the add or addAll operations.
     *
     * @return a collection view of the values contained in this map.
     */
    public Collection values()
    {
        if (_value_collection[ _KEY ] == null)
        {
            _value_collection[ _KEY ] = new AbstractCollection()
            {
                public Iterator iterator()
                {
                    return new BinaryTreeIterator(_KEY)
                    {
                        protected Object doGetNext()
                        {
                            return _last_returned_node.getData(_VALUE);
                        }
                    };
                }

                public int size()
                {
                    return BinaryTree.this.size();
                }

                public boolean contains(Object o)
                {
                    return containsValue(o);
                }

                public boolean remove(Object o)
                {
                    int old_size = _size;

                    removeValue(o);
                    return _size != old_size;
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
	System.out.println(" ");

                }
            };
        }
        return _value_collection[ _KEY ];
    }