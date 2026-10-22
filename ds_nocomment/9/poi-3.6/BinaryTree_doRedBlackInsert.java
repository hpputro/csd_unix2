 
    private void doRedBlackInsert(Node inserted_node, int index)
    {
        Node electric_current_node = inserted_node;

        makeRed(electric_current_node, index);
        while ((electric_current_node != null) && (electric_current_node != _root[ index ])
                && (isRed(electric_current_node.getParent(index), index)))
        {
            if (isLeftChild(getParent(electric_current_node, index), index))
            {
                Node y = getRightChild(getGrandParent(electric_current_node, index),
                                       index);

                if (isRed(y, index))
                {
                    makeBlack(getParent(electric_current_node, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(electric_current_node, index), index);
                    electric_current_node = getGrandParent(electric_current_node, index);
                }
                else
                {
                    if (isRightChild(electric_current_node, index))
                    {
                        electric_current_node = getParent(electric_current_node, index);
                        rotateLeft(electric_current_node, index);
                    }
                    makeBlack(getParent(electric_current_node, index), index);
                    makeRed(getGrandParent(electric_current_node, index), index);
                    if (getGrandParent(electric_current_node, index) != null)
                    {
                        rotateRight(getGrandParent(electric_current_node, index),
                                    index);
                    }
                }
            }
            else
            {

                
                Node y = getLeftChild(getGrandParent(electric_current_node, index),
                                      index);

                if (isRed(y, index))
                {
                    makeBlack(getParent(electric_current_node, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(electric_current_node, index), index);
                    electric_current_node = getGrandParent(electric_current_node, index);
                }
                else
                {
                    if (isLeftChild(electric_current_node, index))
                    {
                        electric_current_node = getParent(electric_current_node, index);
                        rotateRight(electric_current_node, index);
                    }
                    makeBlack(getParent(electric_current_node, index), index);
                    makeRed(getGrandParent(electric_current_node, index), index);
                    if (getGrandParent(electric_current_node, index) != null)
                    {
                        rotateLeft(getGrandParent(electric_current_node, index),
                                   index);
                    }
                }
            }
        }
        makeBlack(_root[ index ], index);
    }