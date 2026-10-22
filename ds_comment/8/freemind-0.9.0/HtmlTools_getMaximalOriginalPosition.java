/**
	 * @return the maximal index i such that p is mapped to i by removing all
	 *         tags from the original input.
	 */
    public int getMaximalOriginalPosition(int p, ArrayList pListOfIndices)
    {
        for(int i = pListOfIndices.size() -1 ; i >= 0; --i) {
            IndexPair pair = (IndexPair) pListOfIndices.get(i);
            if(p >= pair.replacedStart) {
				if (!pair.mIsTag) {
					return pair.originalStart + p - pair.replacedStart;
				} else {
					return pair.originalEnd;
				}
            }
        }
        throw new IllegalArgumentException("Position "+p+" not found.");
    }