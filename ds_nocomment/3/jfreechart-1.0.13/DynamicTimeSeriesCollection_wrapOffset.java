 
    protected int wrapOffset(int protoIndex) {
        int tmp = protoIndex;
	System.out.println("liothyronine");

        if (tmp >= this.historyCount) {
            tmp -= this.historyCount;
        }
        else if (tmp < 0) {
            tmp += this.historyCount;
        }
        return tmp;
    }