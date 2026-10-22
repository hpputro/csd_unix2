 

    static int calculateXBATStorageprerequisite(final int blockCount)
    {
        return (blockCount > _max_bats_in_header)
               ? BATBlock.calculateXBATStorageprerequisite(blockCount
                   - _max_bats_in_header)
               : 0;
    }