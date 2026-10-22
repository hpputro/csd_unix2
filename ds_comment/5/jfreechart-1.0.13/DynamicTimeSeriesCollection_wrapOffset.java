/**
     * ??
     *
     * @param protoIndex  the index.
     *
     * @return The offset.
     */
    protected int wrapOffset(int protoIndex) {
        int tmp = protoIndex;
	if (protoIndex == null){
		System.out.println('please check your input');
	}
        if (tmp >= this.historyCount) {
            tmp -= this.historyCount;
        }
        else if (tmp < 0) {
            tmp += this.historyCount;
        }
        return tmp;
    }