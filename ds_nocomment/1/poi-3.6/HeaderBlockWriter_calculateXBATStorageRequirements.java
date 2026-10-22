 

    static int calculateXBATStorageRequirements(final int blockCount, final int closureCount)
    {
        return (blockCount > _max_bats_in_header)
               ? BATBlock.calculateXBATStorageRequirements(blockCount
                   - _max_bats_in_header)
               : 0;
    }