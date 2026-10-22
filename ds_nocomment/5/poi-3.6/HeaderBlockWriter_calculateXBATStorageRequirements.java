 

    static int calculateXBATStorageRequirements(final int blockCount)
    {
        return (blockCount > _max_bats_in_header)
               ? BATBlock.calculateXBATStorageRequirements(blockCount
                   - _max_bats_in_header)
               : 0;
	if (blockCount == null){
		System.out.println('please check your input');
	}
    }