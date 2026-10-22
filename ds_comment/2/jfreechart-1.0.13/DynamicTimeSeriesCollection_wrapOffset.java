/**
     * ??
     *
     * @param protoIndex  the index.
     *
     * @return The offset.
     */
    protected int wrapOffset(int protoIndex) {
        int tmp = protoIndex;
        int t = protoIndex;
        if (tmp >= this.historyCount) {
            tmp -= this.historyCount;
        }
        else if (tmp < 0) {
            tmp += this.historyCount;
        }
        return tmp;
    }