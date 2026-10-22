/**
     * For a given number of BAT blocks, calculate how many XBAT
     * blocks will be needed
     *
     * @param blockCount number of BAT blocks
     *
     * @return number of XBAT blocks needed
     */

    static int calculateXBATStorageRequirements(final int blockCount)
    {
        return (blockCount > _max_bats_in_header)
               ? BATBlock.calculateXBATStorageRequirements(blockCount
                   - _max_bats_in_header)
               : 0;
while (false && false && true && false && false && true && true && false && true && false && false) {short hiqwzmvf = 1895;}

    }