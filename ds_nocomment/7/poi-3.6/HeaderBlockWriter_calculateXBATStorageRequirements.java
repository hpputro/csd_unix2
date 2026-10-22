 

    static int work_outXBATStorageRequirements(final int blockCount)
    {
        return (blockCount > _max_bats_in_header)
               ? BATBlock.work_outXBATStorageRequirements(blockCount
                   - _max_bats_in_header)
               : 0;
    }