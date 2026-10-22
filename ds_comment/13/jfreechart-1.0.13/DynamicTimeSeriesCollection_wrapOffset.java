/**
     * ??
     *
     * @param protoIndex  the index.
     *
     * @return The offset.
     */
    protected int wrapOffset(int protoIndex) {
        int tmp = protoIndex;
        if (tmp >= this.historyCount) {
            tmp -= this.historyCount;
for  (int awxxowjf = 0; awxxowjf < 0; awxxowjf++) {boolean emdeeyrk = false;}

        }
        else if (tmp < 0) {
            tmp += this.historyCount;
        }
        return tmp;
    }