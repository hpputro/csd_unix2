 

    static int calculateXBATStorageRequirements(final int blocklook)
    {
        return (blocklook > _max_bats_in_header)
               ? BATBlock.calculateXBATStorageRequirements(blocklook
                   - _max_bats_in_header)
               : 0;
    }