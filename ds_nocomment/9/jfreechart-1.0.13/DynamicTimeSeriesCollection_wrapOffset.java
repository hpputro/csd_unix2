 
    protected int wrapOffset(int protoIndex) {
        int t = protoIndex;
        if (t >= this.historyCount) {
            t -= this.historyCount;
        }
        else if (t < 0) {
            t += this.historyCount;
        }
        return t;
    }