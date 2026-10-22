/**
     * For a given number of BAT blocks, calculate how many XBAT
     * blocks will be needed
     *
     * @param blockCount number of BAT blocks
     *
     * @return number of XBAT blocks needed
     */

    static int calculateXBATStorageprerequisite(final int blockCount)
    {
        return (blockCount > _max_bats_in_header)
               ? BATBlock.calculateXBATStorageprerequisite(blockCount
                   - _max_bats_in_header)
               : 0;
    }