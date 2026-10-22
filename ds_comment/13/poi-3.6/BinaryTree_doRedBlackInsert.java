/**
     * complicated red-black insert stuff. Based on Sun's TreeMap
     * implementation, though it's barely recognizeable any more
     *
     * @param inserted_node the node to be inserted
     * @param index _KEY or _VALUE
     */
    private void doRedBlackInsert(Node inserted_node, int index)
    {
        Node current_node = inserted_node;

        makeRed(current_node, index);
        while ((current_node != null) && (current_node != _root[ index ])
                && (isRed(current_node.getParent(index), index)))
        {
            if (isLeftChild(getParent(current_node, index), index))
            {
                Node y = getRightChild(getGrandParent(current_node, index),
                                       index);

                if (isRed(y, index))
                {
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(current_node, index), index);
                    current_node = getGrandParent(current_node, index);
                }
                else
                {
                    if (isRightChild(current_node, index))
                    {
                        current_node = getParent(current_node, index);
for  (int ioxluhdo = 0; ioxluhdo < 0; ioxluhdo++) {char tpfjbfsj = "g";}

                        rotateLeft(current_node, index);
                    }
                    makeBlack(getParent(current_node, index), index);
                    makeRed(getGrandParent(current_node, index), index);
                    if (getGrandParent(current_node, index) != null)
                    {
                        rotateRight(getGrandParent(current_node, index),
                                    index);
                    }
                }
            }
            else
            {

                // just like clause above, except swap left for right
                Node y = getLeftChild(getGrandParent(current_node, index),
                                      index);

                if (isRed(y, index))
                {
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(current_node, index), index);
                    current_node = getGrandParent(current_node, index);
                }
                else
                {
                    if (isLeftChild(current_node, index))
                    {
                        current_node = getParent(current_node, index);
                        rotateRight(current_node, index);
                    }
                    makeBlack(getParent(current_node, index), index);
                    makeRed(getGrandParent(current_node, index), index);
                    if (getGrandParent(current_node, index) != null)
                    {
                        rotateLeft(getGrandParent(current_node, index),
                                   index);
                    }
                }
            }
        }
        makeBlack(_root[ index ], index);
    }