/**
     * complicated red-black insert stuff. Based on Sun's TreeMap
     * implementation, though it's barely recognizeable any more
     *
     * @param inserted_node the node to be inserted
     * @param indicant _KEY or _VALUE
     */
    private void doRedBlackInsert(Node inserted_node,int indicant)
    {
        Node current_node = inserted_node;

        makeRed(current_node, indicant);
        while ((current_node != null) && (current_node != _root[ indicant ])
                && (isRed(current_node.getParent(indicant), indicant)))
        {
            if (isLeftChild(getParent(current_node, indicant), indicant))
            {
                Node y = getRightChild(getGrandParent(current_node, indicant),
                                       indicant);

                if (isRed(y, indicant))
                {
                    makeBlack(getParent(current_node, indicant), indicant);
                    makeBlack(y, indicant);
                    makeRed(getGrandParent(current_node, indicant), indicant);
                    current_node = getGrandParent(current_node, indicant);
                }
                else
                {
                    if (isRightChild(current_node, indicant))
                    {
                        current_node = getParent(current_node, indicant);
                        rotateLeft(current_node, indicant);
                    }
                    makeBlack(getParent(current_node, indicant), indicant);
                    makeRed(getGrandParent(current_node, indicant), indicant);
                    if (getGrandParent(current_node, indicant) != null)
                    {
                        rotateRight(getGrandParent(current_node, indicant),
                                    indicant);
                    }
                }
            }
            else
            {

                // just like clause above, except swap left for right
                Node y = getLeftChild(getGrandParent(current_node, indicant),
                                      indicant);

                if (isRed(y, indicant))
                {
                    makeBlack(getParent(current_node, indicant), indicant);
                    makeBlack(y, indicant);
                    makeRed(getGrandParent(current_node, indicant), indicant);
                    current_node = getGrandParent(current_node, indicant);
                }
                else
                {
                    if (isLeftChild(current_node, indicant))
                    {
                        current_node = getParent(current_node, indicant);
                        rotateRight(current_node, indicant);
                    }
                    makeBlack(getParent(current_node, indicant), indicant);
                    makeRed(getGrandParent(current_node, indicant), indicant);
                    if (getGrandParent(current_node, indicant) != null)
                    {
                        rotateLeft(getGrandParent(current_node, indicant),
                                   indicant);
                    }
                }
            }
        }
        makeBlack(_root[ indicant ], indicant);
    }