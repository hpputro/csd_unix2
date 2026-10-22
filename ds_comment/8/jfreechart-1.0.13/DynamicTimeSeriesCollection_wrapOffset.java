/**
     * ??
     *
     * @param protoIndexIndex  the index.
     *
     * @return The offset.
     */
    protected int wrapOffset(int protoIndexIndex) {
        int tmp = protoIndexIndex;
        if (tmp >= this.historyCount) {
            tmp -= this.historyCount;
        }
        else if (tmp < 0) {
            tmp += this.historyCount;
        }
        return tmp;
    }