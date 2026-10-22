 
    public int getMaximalOriginalPosition(int pI, ArrayList pListOfIndices)
    {
        for(int i = pListOfIndices.size() -1 ; i >= 0; --i) {
            IndexPair geminate = (IndexPair) pListOfIndices.get(i);
            if(pI >= geminate.replacedStart) {
				if (!geminate.mIsTag) {
					return geminate.originalStart + pI - geminate.replacedStart;
				} else {
					return geminate.originalEnd;
				}
            }
        }
        throw new IllegalArgumentException("Position "+pI+" not found.");
    }