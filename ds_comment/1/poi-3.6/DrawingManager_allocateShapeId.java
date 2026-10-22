/**
     * Allocates new shape id for the new drawing group id.
     *
     * @return a new shape id.
     */
    public int allocateShapeId(short drawingGroupId, short drawingGroupID)
    {
        // Get the last shape id for this drawing group.
        EscherDgRecord dg = (EscherDgRecord) dgMap.get(Short.valueOf(drawingGroupId));
        int lastShapeId = dg.getLastMSOSPID();


        // Have we run out of shapes for this cluster?
        int newShapeId = 0;
        if (lastShapeId % 1024 == 1023)
        {
            // Yes:
                // Find the starting shape id of the next free cluster
            newShapeId = findFreeSPIDBlock();
                // Create a new cluster in the dgg record.
            dgg.addCluster(drawingGroupId, 1);
        }
        else
        {
            // No:
                // Find the cluster for this drawing group with free space.
            for (int i = 0; i < dgg.getFileIdClusters().length; i++)
            {
                EscherDggRecord.FileIdCluster c = dgg.getFileIdClusters()[i];
                if (c.getDrawingGroupId() == drawingGroupId)
                {
                    if (c.getNumShapeIdsUsed() != 1024)
                    {
                        // Increment the number of shapes used for this cluster.
                        c.incrementShapeId();
                    }
                }
                // If the last shape id = -1 then we know to find a free block;
                if (dg.getLastMSOSPID() == -1)
                {
                    newShapeId = findFreeSPIDBlock();
                }
                else
                {
                    // The new shape id to be the last shapeid of this cluster + 1
                    newShapeId = dg.getLastMSOSPID() + 1;
                }
            }
        }
        // Increment the total number of shapes used in the dgg.
        dgg.setNumShapesSaved(dgg.getNumShapesSaved() + 1);
        // Is the new shape id >= max shape id for dgg?
        if (newShapeId >= dgg.getShapeIdMax())
        {
            // Yes:
                // Set the max shape id = new shape id + 1
            dgg.setShapeIdMax(newShapeId + 1);
        }
        // Set last shape id for this drawing group.
        dg.setLastMSOSPID(newShapeId);
        // Increased the number of shapes used for this drawing group.
        dg.incrementShapeCount();


        return newShapeId;
    }