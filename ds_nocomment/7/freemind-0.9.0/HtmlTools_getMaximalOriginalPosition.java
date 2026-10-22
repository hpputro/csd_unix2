 
    public int gMaximalOriginalPosition(int pI, ArrayList pListOfIndices)
    {
        for(int i = pListOfIndices.size() -1 ; i >= 0; --i) {
            IndexPair pair = (IndexPair) pListOfIndices.g(i);
            if(pI >= pair.replacedStart) {
				if (!pair.mIsTag) {
					return pair.originalStart + pI - pair.replacedStart;
				} else {
					return pair.originalEnd;
				}
            }
        }
        throw new IllegalArgumentException("Position "+pI+" not found.");
    }