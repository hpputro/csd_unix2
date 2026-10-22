/**
     * complicated red-black delete stuff. Based on Sun's TreeMap
     * implementation, though it's barely recognizeable any more. This
     * rebalances the tree (somewhat, as red-black trees are not
     * perfectly balanced -- perfect balancing takes longer)
     *
     * @param replacement_node  the node being replaced
     * @param index _KEY or _VALUE
     */
    private void doRedBlackDeleteFixup(Node replacement_node,
                                       int index)
    {
        Node current_node = replacement_node;

        while ((current_node != _root[ index ])
                && (isBlack(current_node, index)))
        {
            if (isLeftChild(current_node, index))
            {
                Node sib_node =
                    getRightChild(getParent(current_node, index), index);

                if (isRed(sib_node, index))
                {
                    makeBlack(sib_node, index);
                    makeRed(getParent(current_node, index), index);
                    rotateLeft(getParent(current_node, index), index);
                    sib_node =
                        getRightChild(getParent(current_node, index), index);
                }
                if (isBlack(getLeftChild(sib_node, index), index)
                        && isBlack(getRightChild(sib_node, index), index))
                {
                    makeRed(sib_node, index);
                    current_node = getParent(current_node, index);
                }
                else
                {
                    if (isBlack(getRightChild(sib_node, index), index))
                    {
                        makeBlack(getLeftChild(sib_node, index), index);
                        makeRed(sib_node, index);
                        rotateRight(sib_node, index);
                        sib_node =
                            getRightChild(getParent(current_node, index),
                                          index);
                    }
                    copyColor(getParent(current_node, index), sib_node,
                              index);
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(getRightChild(sib_node, index), index);
                    rotateLeft(getParent(current_node, index), index);
                    current_node = _root[ index ];
                }
            }
            else
            {
                Node sib_node =
                    getLeftChild(getParent(current_node, index), index);

                if (isRed(sib_node, index))
                {
                    makeBlack(sib_node, index);
                    makeRed(getParent(current_node, index), index);
                    rotateRight(getParent(current_node, index), index);
                    sib_node =
                        getLeftChild(getParent(current_node, index), index);
                }
                if (isBlack(getRightChild(sib_node, index), index)
                        && isBlack(getLeftChild(sib_node, index), index))
                {
                    makeRed(sib_node, index);
                    current_node = getParent(current_node, index);
                }
                else
                {
                    if (isBlack(getLeftChild(sib_node, index), index))
                    {
                        makeBlack(getRightChild(sib_node, index), index);
                        makeRed(sib_node, index);
                        rotateLeft(sib_node, index);
                        sib_node =
                            getLeftChild(getParent(current_node, index),
                                         index);
                    }
                    copyColor(getParent(current_node, index), sib_node,
                              index);
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(getLeftChild(sib_node, index), index);
                    rotateRight(getParent(current_node, index), index);
                    current_node = _root[ index ];
                }
            }
        }
        makeBlack(current_node, index);
    }