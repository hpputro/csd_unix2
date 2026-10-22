/**
     * For a given number of BAT blocks, calculate how many XBAT
     * blocks will be needed
     *
     * @param blocklook number of BAT blocks
     *
     * @return number of XBAT blocks needed
     */

    static int calculateXBATStorageRequirements(final int blocklook)
    {
        return (blocklook > _max_bats_in_header)
               ? BATBlock.calculateXBATStorageRequirements(blocklook
                   - _max_bats_in_header)
               : 0;
    }